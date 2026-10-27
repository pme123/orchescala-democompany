# democompany-patterns

Katalog mit Beispielprozessen für die **Orchescala-Patterns** – kleine BPMN-Bausteine, die die
Orch Spec in einen Prozess einfügt und wiedererkennt. Je Pattern gibt es einen Beispielprozess
für Camunda 7 und Camunda 8 und eine Simulation je Engine, die den Baustein auf einer echten Engine
durchspielt. Später sollen die BPMN auch als Testdaten für die Orch Spec dienen.

Bis jetzt: **«Init Process»**.

## Init Process – `democompany-patterns-initProcessV1`

```
Start ─▶ [Init Process] ─▶ (succeeded)
              │ output-mocked (Fehler-Boundary)
              ▼
         ⇨ output-mocked (Link-Wurf)

⇨ output-mocked (Link-Ziel) ─▶ (output-mocked)
```

Der erste Schritt des Prozesses ist ein Service-Task mit der **Prozess-ID als Topic**. Den Task
bearbeitet der InitWorker (`InitProcessWorker`):

- er **prüft das In** – ein leerer `customerName` ist ungültig (`validation-failed`),
- er **setzt die Vorgaben aus dem InConfig** – fehlt `inConfig` im In, steht danach
  `inConfig = {"greeting": "Grüezi"}` im Prozess,
- er setzt die Variablen aus `customInit` (hier `greetingMessage = "Grüezi Peter Muster"`),
- ist der Prozess **gemockt** (`_mockedWorkers` enthält die Prozess-ID), liefert er die gemockte
  Ausgabe als Fehler `output-mocked`. Dieser ist in `_handledErrors = output-mocked` freigegeben:
  die Boundary springt über den Link `output-mocked` zum Ende mit `processStatus = output-mocked`.

| Datei | Inhalt |
|---|---|
| [`InitProcess.scala`](01-domain/src/main/scala/democompany/patterns/domain/initProcess/v1/InitProcess.scala) | `In`, `InConfig`, `InitIn`, `Out` |
| [`InitProcessWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/initProcess/v1/InitProcessWorker.scala) | InitWorker: Prüfung und `customInit` |
| [`camunda/patterns-initProcessV1.bpmn`](src/main/resources/camunda/patterns-initProcessV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-initProcessV1.bpmn`](src/main/resources/camunda8/patterns-initProcessV1.bpmn) | Camunda 8 |
| [`InitProcessSimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/InitProcessSimulation.scala) | die Szenarien, für beide Engines gleich |

Layout und IDs des Pattern-Blocks (`OutputMockedBoundary`, `OutputMockedThrow`,
`OutputmockedEvent`, `OutputmockedEndEvent`, `OutputMockedError`) stammen aus dem Pattern
`init-process` der Orch Spec.

### Was in Camunda 8 anders ist

| | Camunda 7 | Camunda 8 |
|---|---|---|
| Topic | `camunda:type="external" camunda:topic="<processId>"` | `zeebe:taskDefinition type="<processId>"` |
| `_handledErrors` | `camunda:inputParameter` | `zeebe:input source="=&quot;output-mocked&quot;"` |
| `processStatus` am Ende | Execution Listener (`execution.setVariable(...)`) | Output-Mapping (`zeebe:output`) |
| `processInstanceId` | gibt es in der Engine (`execution.processInstanceId`) | setzt der InitWorker als Variable (der `processInstanceKey`) – nur im normalen Lauf, siehe unten |

In Camunda 8 landen die Variablen der gemockten Ausgabe (der Fehler `output-mocked`) zuerst im
Scope der Boundary (dort steht `processStatus = succeeded` aus dem gemockten `Out`); im Prozess
steht danach `processStatus = output-mocked` aus dem Output-Mapping des Endes.

## Simulationen

Drei Szenarien je Engine:

1. **`InitProcess`** – normaler Lauf: endet mit `processStatus = succeeded`, `inConfig` mit den Vorgaben.
2. **`InitProcess mocked`** – Prozess-Output gemockt: endet über den Pfad `output-mocked` mit
   `processStatus = output-mocked`.
3. **`InitProcess invalid In`** – `customerName = " "`: der InitWorker meldet
   `validation-failed: customerName must not be empty.` – nicht freigegeben, also ein Incident.

Die Simulation startet den Prozess **ohne Prüfung über die Worker-App**
(`config.validateProcess(false)`): sonst lehnt der Start ein ungültiges In schon vor dem Prozess ab,
und Szenario 3 käme nie zum InitWorker in der Engine.

### Engines

Beide über die Firmen-Einstellung:

- **Camunda 7**: `CAMUNDA_BASE_URL` (Standard `http://localhost:8080/engine-rest`, Token über `SSO_BASE_URL`).
- **Camunda 8**: wie bei Valiant ein **lokales c8run ohne Anmeldung** – `CAMUNDA_C8_GRPC_URL`
  (Standard `http://localhost:26500`), `CAMUNDA_C8_REST_URL` (Standard `http://localhost:8181`),
  `CAMUNDA_C8_OPERATE_URL`.

Gateway und Worker-Apps nutzen die Engines aus `COMPANY_ENGINES` (Standard `C7,C8,Op`).

### Starten

1. Gateway der Firma starten – im Ordner `democompany-orchescala` (Port 8887, ein anderer mit
   `GATEWAY_PORT`):
   ```bash
   COMPANY_ENGINES=C7,C8 sbt gateway/run
   ```
2. Worker starten (C7 und C8 in einer App). `WORKER_APP_PORT`, wenn 5555 schon belegt ist:
   ```bash
   COMPANY_ENGINES=C7,C8 WORKER_APP_PORT=5556 sbt "worker/runMain democompany.patterns.worker.WorkerApp"
   ```
3. Deployen und simulieren in einem Schritt – wie bei Valiant, nur über den Gateway statt BPF:
   ```bash
   ./helper.scala deploy InitProcessC7Simulation
   ```
   ```bash
   ./helper.scala deploy InitProcessC8Simulation
   ```
   Der Helper der Firma (`CompanyDeployHelper`) macht `sbt publishLocal`, schickt das Manifest
   `democompany:democompany-patterns:0.1.0-SNAPSHOT` an `POST <GATEWAY_URL>/deployment?targetEngine=<C7|C8>`
   (die Engine aus dem Namen der Simulation; der Gateway liest die BPMN aus dem Jar in
   `~/.ivy2/local`, Ordner `camunda` bzw. `camunda8`) und startet danach die Simulation.
   `GATEWAY_URL` (Standard `http://localhost:8887`) für einen anderen Gateway; `GATEWAY_TOKEN` ist nur
   nötig, wenn der Gateway Tokens prüft (`TOKEN_ISSUER`).

## Nächste Patterns

### Identität übernehmen / zurücksetzen

Zwei getrennte Patterns – wie in C7 opt-in: nur wo das Pattern steht, wechselt die Identität.

- **«Identität übernehmen»** – an einer Benutzeraufgabe (beim Abschluss) oder am Empfang einer Nachricht.
  - C7 heute: Listener mit Skript
    `impersonateUserIdTemp = impersonateUserId; impersonateUserId = <angemeldeter Benutzer>`.
  - Neu mit `_identityCorrelation`: `_identityCorrelationBefore = _identityCorrelation`,
    `_identityCorrelation = <Identität der handelnden Person>`.
- **«Identität zurücksetzen»** – überall möglich.
  - C7 heute: `impersonateUserId = impersonateUserIdTemp`.
  - Neu: Output-Mapping `_identityCorrelation = _identityCorrelationBefore`.

**C8:** die Output-Mappings stehen auf einem leeren Zwischenereignis direkt danach – nicht auf der
Benutzeraufgabe, dem Nachrichten-Ereignis oder dem Service-Task (mit einem Output-Mapping übernimmt
Zeebe dort sonst nur noch die gemappten Variablen).

**Voraussetzung in Orchescala (Gateway):** `_identityCorrelation` nur beim Start setzen; beim
Abschluss einer Aufgabe und beim Senden einer Nachricht die signierte Identität der handelnden Person
unter eigenem Namen mitgeben (Arbeitsname `_identityCorrelationOfActor`).
Offen: Nachrichten mit Korrelation über den businessKey – das Gateway kennt die Instanz (für die
Signatur) erst nach der Korrelation.

## Offene Punkte

- Im gemockten Lauf auf C8 setzt der InitWorker `processInstanceId` nicht (im normalen Lauf schon) –
  die Variablen kommen dort über den Fehler `output-mocked`, nicht über das Abschliessen des Jobs.
