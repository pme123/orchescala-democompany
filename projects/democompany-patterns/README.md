# democompany-patterns

Katalog mit Beispielprozessen für die **Orchescala-Patterns** – kleine BPMN-Bausteine, die die
Orch Spec in einen Prozess einfügt und wiedererkennt. Je Pattern gibt es einen Beispielprozess
für Camunda 7 und Camunda 8 und eine Simulation je Engine, die den Baustein auf einer echten Engine
durchspielt. Später sollen die BPMN auch als Testdaten für die Orch Spec dienen.

Bis jetzt: **«Init Process»**, **«Identität übernehmen / zurücksetzen»** und
**«Mocks und Identität weitergeben»**.

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
- **Camunda 8**: ein **lokales c8run ohne Anmeldung** – `CAMUNDA_C8_GRPC_URL`
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
3. Deployen und simulieren in einem Schritt – über den Gateway:
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
Start ─▶ [Init Process] ─▶ [Antrag freigeben] ─▶ ◯ Identität übernehmen ─▶ [Identität prüfen]
      ─▶ [Antrag abschliessen] ─▶ ◯ Identität zurücksetzen ─▶ [Identität prüfen] ─▶ (succeeded)
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
Kopieren prüfen die Worker sie wie bisher. Das zeigt **«Identität prüfen»**: ein ServiceWorker nach dem
Übernehmen und nach dem Zurücksetzen. Er ist im BPMN gemockt (`_servicesMocked = true`), prüft aber
vorher die Signatur von `_identityCorrelation` – eine ungültige lässt den Job scheitern. Den
Benutzernamen dieser Identität hängt er an `checkedUsernames` an; das BPMN mappt ihn als Input
`identityUsername` (C7 `${_identityCorrelation.prop("username").stringValue()}`, C8
`=_identityCorrelation.username`), denn ein ServiceWorker kommt selbst nicht an die Identität.

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
| [`CheckIdentity.scala`](01-domain/src/main/scala/democompany/patterns/domain/identity/v1/CheckIdentity.scala) | «Identität prüfen» – Service-Aufruf mit der Identität des Prozesses |
| [`CheckIdentityWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/identity/v1/CheckIdentityWorker.scala) | der ServiceWorker dazu (gemockt) |
| [`CloseRequestUT.scala`](01-domain/src/main/scala/democompany/patterns/domain/identity/v1/CloseRequestUT.scala) | «Antrag abschliessen» – hier hat der Prozess die übernommene Identität |
| [`IdentityWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/identity/v1/IdentityWorker.scala) | InitWorker: Prüfung des In |
| [`camunda/patterns-identityV1.bpmn`](src/main/resources/camunda/patterns-identityV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-identityV1.bpmn`](src/main/resources/camunda8/patterns-identityV1.bpmn) | Camunda 8 |
| [`IdentitySimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/IdentitySimulation.scala) | die Szenarien, für beide Engines gleich |

### Simulationen

1. **`Identity approved`** – gestartet als `admin`, «Antrag freigeben» erledigt als `actor`
   (`.actingAs(testActorIdentityCorrelation)`), «Antrag abschliessen» wieder als `admin`.
   «Identität prüfen» sieht zuerst `actor` (übernommen), dann `admin` (zurückgesetzt):
   `checkedUsernames = [actor, admin]`. Am Ende gibt es `_identityCorrelation`,
   `_identityCorrelationBefore` und `_identityCorrelationOfActor`.
2. **`Identity mocked`** – endet über `output-mocked`, ohne Benutzeraufgabe.
3. **`Identity invalid In`** – `customerName = " "`: Incident `validation-failed`.

```bash
./helper.scala deploy IdentityC7Simulation
```
```bash
./helper.scala deploy IdentityC8Simulation
```

Mit Prüfungen wie `isEquals` oder `exists` vergleicht die Simulation nur diese – nicht das `Out` von
`example`. Ohne Zurücksetzen wäre `checkedUsernames = [actor, actor]` – das lässt das Szenario
scheitern (so geprüft).

Vor `actingAs` von Hand geprüft (C7 und C8): über ein lokales Gateway gestartet als `starter`,
«Antrag freigeben» erledigt als `approver`:

| wann | `_identityCorrelation` |
|---|---|
| bei «Antrag freigeben» | `starter` |
| bei «Antrag abschliessen» | `approver` (übernommen) |
| am Ende | `starter` (zurückgesetzt) – `_identityCorrelationBefore = starter` |

«Identität prüfen» hat dabei beide Male die Signatur des Gateways akzeptiert (`approver`, dann `starter`).

### Voraussetzung: Orchescala

`_identityCorrelationOfActor` setzt das Gateway erst ab dem Orchescala-Branch
`claude/gateway-actor-identity-b424f0` (0.9.0-SNAPSHOT, noch nicht gemergt). Mit einem älteren
Gateway überschreibt der Abschluss einer Aufgabe die Identität selbst: C8 immer, C7 nur mit
`doOverrideImpersonation = true`.

Dass `example.exists(...)` das Szenario nach dem `val` benennt (`Identity approved`), braucht
Orchescala ab pme123/orchescala#87 (in develop) – vorher hiess es `example`.

`actingAs` und die zweite Test-Identität `testActorIdentityCorrelation` gibt es ab dem
Orchescala-Branch `feat/simulation-second-test-identity` (0.9.0-SNAPSHOT, noch nicht gemergt).

## Mocks und Identität weitergeben – `democompany-patterns-propagateMocksV1`

```
Start ─▶ [Init Process] ─▶ [Teilprozess aufrufen] ─▶ (succeeded)
                                  │
                                  ▼  democompany-patterns-propagateMocksSubV1
                    Start ─▶ [Init Process] ─▶ [Kunde laden] ─▶ (succeeded)
```

Ein Teilprozess (Call Activity) sieht nur die Variablen, die ihm der Aufruf mitgibt. Damit Mocks
und Identität auch dort gelten, gibt die Call Activity diese drei weiter:

- **`_servicesMocked`** – die Services im Teilprozess sind gemockt,
- **`_mockedWorkers`** – gemockte Worker, auch der InitWorker des Teilprozesses (dessen Prozess-ID),
- **`_identityCorrelation`** – die signierte Identität des Starts.

Dazu das In (`customerName`, `clientId`); zurück kommen `processStatus` des Teilprozesses als
`subProcessStatus` und `client`.

**«Kunde laden»** zeigt, dass beides ankommt: der Service `GetClientsClientId` aus
**democompany-services** (Topic `democompany-services-clientsV1-GetClientsClientId`, wie in
democompany-cards). Seinen ServiceWorker führt die Worker-App dieses Projekts mit
(`dependencies(democompany.services.worker.WorkerApp)`, die Abhängigkeit steht in `PROJECT.conf`).
Gemockt ist er nur, wenn `_servicesMocked` im Teilprozess angekommen ist – und vorher prüft er die
Signatur von `_identityCorrelation`. Die Identität ist an die Prozessinstanz des **Starts**
gebunden; im Teilprozess akzeptiert der Worker sie, weil das die Root-Instanz seines Jobs ist.

Weil das Topic nicht mit der Prozess-ID beginnt, sieht die Orch Spec den Schritt als Service aus dem
Katalog – nicht als eigenen Worker des Prozesses.

| | Camunda 7 | Camunda 8 |
|---|---|---|
| Aufruf | `calledElement="<processId>"` | `zeebe:calledElement processId="<processId>"` mit `propagateAllChildVariables="false"` und `propagateAllParentVariables="false"` |
| hinein | `camunda:in source=… target=…` | `zeebe:input` |
| zurück | `camunda:out source=… target=…` | `zeebe:output` |
| Root-Instanz des Jobs | der Worker liest sie aus der History | kommt mit dem Job – über gRPC nicht, dann fragt der Worker die Engine (siehe unten) |

| Datei | Inhalt |
|---|---|
| [`PropagateMocks.scala`](01-domain/src/main/scala/democompany/patterns/domain/propagateMocks/v1/PropagateMocks.scala) | `In`, `Out` des aufrufenden Prozesses |
| [`PropagateMocksSub.scala`](01-domain/src/main/scala/democompany/patterns/domain/propagateMocks/v1/PropagateMocksSub.scala) | `In`, `Out` des Teilprozesses |
| [`PropagateMocksWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/propagateMocks/v1/PropagateMocksWorker.scala) | InitWorker: Prüfung des In |
| [`PropagateMocksSubWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/propagateMocks/v1/PropagateMocksSubWorker.scala) | InitWorker des Teilprozesses |
| [`camunda/patterns-propagateMocksV1.bpmn`](src/main/resources/camunda/patterns-propagateMocksV1.bpmn), [`camunda/patterns-propagateMocksSubV1.bpmn`](src/main/resources/camunda/patterns-propagateMocksSubV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-propagateMocksV1.bpmn`](src/main/resources/camunda8/patterns-propagateMocksV1.bpmn), [`camunda8/patterns-propagateMocksSubV1.bpmn`](src/main/resources/camunda8/patterns-propagateMocksSubV1.bpmn) | Camunda 8 |
| [`PropagateMocksSimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/PropagateMocksSimulation.scala) | die Szenarien, für beide Engines gleich |

### Simulationen

1. **`PropagateMocks services mocked`** – `.mockServices`: «Kunde laden» ist im Teilprozess
   gemockt und akzeptiert die Identität des Starts; endet mit `subProcessStatus = succeeded` und dem
   Kunden aus dem Mock (`client`).
2. **`PropagateMocks sub worker mocked`** – zusätzlich `.mockWorker(PropagateMocksSub.processName)`:
   der Teilprozess endet über `output-mocked` (`subProcessStatus = output-mocked`), der aufrufende
   Prozess mit `processStatus = succeeded`.
3. **`PropagateMocks invalid In`** – `customerName = " "`: Incident `validation-failed`.

```bash
./helper.scala deploy PropagateMocksC7Simulation
```
```bash
./helper.scala deploy PropagateMocksC8Simulation
```

### Voraussetzung: Orchescala (C8)

Über gRPC liefert der Camunda-Client 8.9 die Root-Instanz eines Jobs nicht mit
(`rootProcessInstanceKey` ist leer). Ohne sie lehnt der Worker im Teilprozess die Identität des
Starts ab («bound to process instance …, not to the job's … (root: -)»). Ab dem Orchescala-Branch
`fix/c8-root-process-instance` (0.9.0-SNAPSHOT, noch nicht gemergt) fragt der C8-Worker die
Root-Instanz dann bei der Engine nach (`GET /v2/process-instances/{key}`). C7 braucht das nicht.

## Offene Punkte

- Im gemockten Lauf auf C8 setzt der InitWorker `processInstanceId` nicht (im normalen Lauf schon) –
  die Variablen kommen dort über den Fehler `output-mocked`, nicht über das Abschliessen des Jobs.
