# democompany-patterns

Katalog mit Beispielprozessen für die **Orchescala-Patterns** – kleine BPMN-Bausteine, die die
Orch Spec in einen Prozess einfügt und wiedererkennt. Je Pattern gibt es einen Beispielprozess
für Camunda 7 und Camunda 8 und eine Simulation je Engine, die den Baustein auf einer echten Engine
durchspielt. Später sollen die BPMN auch als Testdaten für die Orch Spec dienen.

Bis jetzt: **«Init Process»** und **«Identität übernehmen / zurücksetzen»**.

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

## Identität übernehmen / zurücksetzen – `democompany-patterns-identityV1`

```
Start ─▶ [Init Process] ─▶ [Antrag freigeben] ─▶ ◯ Identität übernehmen ─▶ [Antrag abschliessen] ─▶ ◯ Identität zurücksetzen ─▶ (succeeded)
```

Zwei getrennte Patterns – opt-in wie in C7: nur wo das Pattern steht, wechselt die Identität.

Das Gateway setzt `_identityCorrelation` nur beim **Start**. Schliesst jemand eine Benutzeraufgabe ab
(oder sendet eine Nachricht mit `processInstanceId`), gibt es dessen signierte Identität als
**`_identityCorrelationOfActor`** mit – `_identityCorrelation` bleibt. Ob der Prozess sie übernimmt,
entscheidet das BPMN (siehe «Voraussetzung: Orchescala»).

- **«Identität übernehmen»** – ein leeres Zwischenereignis direkt nach der Benutzeraufgabe (oder dem
  Empfang der Nachricht):
  `_identityCorrelationBefore = _identityCorrelation`, dann
  `_identityCorrelation = _identityCorrelationOfActor` (fehlt sie, bleibt die bisherige).
- **«Identität zurücksetzen»** – ein leeres Zwischenereignis, überall möglich:
  `_identityCorrelation = _identityCorrelationBefore`.

Die Signatur bindet die Identität an die Prozessinstanz, nicht an den Namen der Variable – nach dem
Kopieren prüfen die Worker sie wie bisher.

| | Camunda 7 | Camunda 8 |
|---|---|---|
| übernehmen | zwei Execution Listener (`start`) mit `execution.setVariable(...)` und `execution.getVariableTyped(...)` | Output-Mappings – die Quellen werden alle vor dem Setzen gelesen |
| zurücksetzen | ein Execution Listener | ein Output-Mapping |

**C8:** die Output-Mappings stehen auf dem leeren Zwischenereignis – nicht auf der Benutzeraufgabe
(mit einem Output-Mapping übernimmt Zeebe dort sonst nur noch die gemappten Variablen).

| Datei | Inhalt |
|---|---|
| [`Identity.scala`](01-domain/src/main/scala/democompany/patterns/domain/identity/v1/Identity.scala) | `In`, `Out` |
| [`ApproveRequestUT.scala`](01-domain/src/main/scala/democompany/patterns/domain/identity/v1/ApproveRequestUT.scala) | «Antrag freigeben» – wer sie erledigt, wird übernommen |
| [`CloseRequestUT.scala`](01-domain/src/main/scala/democompany/patterns/domain/identity/v1/CloseRequestUT.scala) | «Antrag abschliessen» – hier hat der Prozess die übernommene Identität |
| [`IdentityWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/identity/v1/IdentityWorker.scala) | InitWorker: Prüfung des In |
| [`camunda/patterns-identityV1.bpmn`](src/main/resources/camunda/patterns-identityV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-identityV1.bpmn`](src/main/resources/camunda8/patterns-identityV1.bpmn) | Camunda 8 |
| [`IdentitySimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/IdentitySimulation.scala) | die Szenarien, für beide Engines gleich |

### Simulationen

1. **`Identity approved`** – beide Benutzeraufgaben; am Ende gibt es `_identityCorrelation`,
   `_identityCorrelationBefore` und `_identityCorrelationOfActor`.
2. **`Identity mocked`** – endet über `output-mocked`, ohne Benutzeraufgabe.
3. **`Identity invalid In`** – `customerName = " "`: Incident `validation-failed`.

```bash
./helper.scala deploy IdentityC7Simulation
```
```bash
./helper.scala deploy IdentityC8Simulation
```

Die Simulation startet und erledigt alles mit **derselben** Test-Identität (`admin`) – sie zeigt, dass
die Variablen gesetzt werden, aber nicht den Wechsel. Den Wechsel geprüft (C7 und C8): über ein
lokales Gateway gestartet als `starter`, «Antrag freigeben» erledigt als `approver`:

| wann | `_identityCorrelation` |
|---|---|
| bei «Antrag freigeben» | `starter` |
| bei «Antrag abschliessen» | `approver` (übernommen) |
| am Ende | `starter` (zurückgesetzt) – `_identityCorrelationBefore = starter` |

### Voraussetzung: Orchescala

`_identityCorrelationOfActor` setzt das Gateway erst ab dem Orchescala-Branch
`claude/gateway-actor-identity-b424f0` (0.9.0-SNAPSHOT, noch nicht gemergt). Mit einem älteren
Gateway überschreibt der Abschluss einer Aufgabe die Identität selbst: C8 immer, C7 nur mit
`doOverrideImpersonation = true`.

## Offene Punkte

- Im gemockten Lauf auf C8 setzt der InitWorker `processInstanceId` nicht (im normalen Lauf schon) –
  die Variablen kommen dort über den Fehler `output-mocked`, nicht über das Abschliessen des Jobs.
- Die Simulation kennt nur eine Test-Identität – eine andere Identität für eine Benutzeraufgabe oder
  Nachricht fehlt, damit sie den Wechsel der Identität selbst prüfen könnte.
