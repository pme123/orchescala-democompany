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

- **Camunda 7**: die Firmen-Einstellung (`CAMUNDA_BASE_URL`, Standard
  `http://localhost:8080/engine-rest`, Token über `SSO_BASE_URL`).
- **Camunda 8**: ein **lokales c8run ohne Anmeldung** (`PatternsC8Simulation`, `PatternsC8Client`) –
  die Firmen-Einstellung zeigt auf den SaaS-Cluster.
  - `C8_LOCAL_GRPC_URL` (Standard `http://localhost:26500`)
  - `C8_LOCAL_REST_URL` (Standard `http://localhost:8181`)

### Starten

1. BPMN deployen
   - C7 über den Gateway der Firma: er liest die BPMN aus dem Projekt-Jar in `~/.ivy2/local`
     (Ordner `camunda`) und meldet sich selbst bei der Engine an.
     ```bash
     sbt publishLocal
     ```
     Im Ordner `democompany-orchescala` (`GATEWAY_PORT`, wenn 8888 schon belegt ist):
     ```bash
     COMPANY_ENGINES=C7 GATEWAY_PORT=8887 sbt gateway/run
     ```
     ```bash
     curl -X POST "http://localhost:8887/deployment?targetEngine=C7" -H "Authorization: Bearer local-dev" -H "Content-Type: application/json" -d '{"deployments":[{"company":"democompany","project":"democompany-patterns","version":"0.1.0-SNAPSHOT"}]}'
     ```
     Ohne `TOKEN_ISSUER` prüft der Gateway nur, ob ein Token da ist.
   - C8 (c8run): direkt an die Engine – der Gateway der Firma kennt nur den SaaS-Cluster.
     ```bash
     curl -X POST -F "resources=@src/main/resources/camunda8/patterns-initProcessV1.bpmn" http://localhost:8181/v2/deployments
     ```
2. Worker starten (C7 und C8 in einer App). `WORKER_APP_PORT`, wenn 5555 schon belegt ist:
   ```bash
   WORKER_APP_PORT=5556 sbt "worker/runMain democompany.patterns.worker.WorkerApp"
   ```
3. Simulation:
   ```bash
   sbt "simulation/testOnly *InitProcessC8Simulation"
   ```
   ```bash
   sbt "simulation/testOnly *InitProcessC7Simulation"
   ```

## Offene Punkte

- Im gemockten Lauf auf C8 setzt der InitWorker `processInstanceId` nicht (im normalen Lauf schon) –
  die Variablen kommen dort über den Fehler `output-mocked`, nicht über das Abschliessen des Jobs.
- Die Firma kennt kein lokales C8 (nur SaaS) und registriert keinen C8-Worker
  (`CompanyWorkerApp`) – darum die eigenen Klassen `PatternsC8Client` / `PatternsC8Simulation`.
