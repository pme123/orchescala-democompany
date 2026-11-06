# democompany-patterns

Katalog mit Beispielprozessen für die **Orchescala-Patterns** – kleine BPMN-Bausteine, die die
Orch Spec in einen Prozess einfügt und wiedererkennt. Je Pattern gibt es einen Beispielprozess
für Camunda 7 und Camunda 8 und eine Simulation je Engine, die den Baustein auf einer echten Engine
durchspielt. Später sollen die BPMN auch als Testdaten für die Orch Spec dienen.

Bis jetzt: **«Init Process»**, **«Identität übernehmen / zurücksetzen»**,
**«Mocks und Identität weitergeben»**, **«Einmalige Ausführung»**, **«Prozess-Status am Ende»**,
**«Prozess mit Fehler stoppen»**, **«Prozess-Event senden»** und **«Abbruch per Signal»**.

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
| eigene Instanz | gibt es in der Engine (`execution.processInstanceId`) | in FEEL `string(camunda.processInstance.key)` – ab Camunda 8.10, keine Variable |

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
Kopieren prüfen die ServiceWorker sie wie bisher. Welche Identität der Prozess gerade hat, zeigt
**«Identität prüfen»**: ein CustomTask nach dem Übernehmen und nach dem Zurücksetzen. Er hängt den
Benutzernamen dieser Identität an `checkedUsernames` an; das BPMN mappt ihn als Input
`identityUsername` (C7 `${_identityCorrelation.prop("username").stringValue()}`, C8
`=_identityCorrelation.username`). Die Signatur prüft ein CustomWorker nicht – das tun nur
ServiceWorker (siehe «Mocks und Identität weitergeben»). Ein CustomTask ist er, damit die Orch Spec
ihn als Worker des Prozesses mit seinem In/Out erkennt.

| | Camunda 7 | Camunda 8 |
|---|---|---|
| übernehmen | zwei Execution Listener (`start`) mit `execution.setVariable(...)` und `execution.getVariableTyped(...)` | zwei Output-Mappings – **die Reihenfolge zählt**: Zeebe wendet sie nacheinander an, also erst `_identityCorrelationBefore` sichern, dann `_identityCorrelation` überschreiben (umgekehrt wäre «Before» schon der Actor) |
| zurücksetzen | ein Execution Listener | ein Output-Mapping |

**C8:** fehlt eine Variable, liest FEEL `null` – ohne Identität gibt es also keinen Incident, die Variablen stehen dann auf `null` (auf c8run 8.9 nachgeprüft, wie die Reihenfolge). Die Output-Mappings stehen auf dem leeren Zwischenereignis – nicht auf der Benutzeraufgabe
(mit einem Output-Mapping übernimmt Zeebe dort sonst nur noch die gemappten Variablen).

| Datei | Inhalt |
|---|---|
| [`Identity.scala`](01-domain/src/main/scala/democompany/patterns/domain/identity/v1/Identity.scala) | `In`, `Out` |
| [`ApproveRequestUT.scala`](01-domain/src/main/scala/democompany/patterns/domain/identity/v1/ApproveRequestUT.scala) | «Antrag freigeben» – wer sie erledigt, wird übernommen |
| [`CheckIdentity.scala`](01-domain/src/main/scala/democompany/patterns/domain/identity/v1/CheckIdentity.scala) | «Identität prüfen» – CustomTask: Benutzername der Identität des Prozesses |
| [`CheckIdentityWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/identity/v1/CheckIdentityWorker.scala) | der CustomWorker dazu |
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

«Identität prüfen» – damals noch ein ServiceWorker – hat dabei beide Male die Signatur des Gateways
akzeptiert (`approver`, dann `starter`).

### Voraussetzung: Orchescala

`_identityCorrelationOfActor` setzt das Gateway ab pme123/orchescala#90 (in develop,
0.9.0-SNAPSHOT). Mit einem älteren
Gateway überschreibt der Abschluss einer Aufgabe die Identität selbst: C8 immer, C7 nur mit
`doOverrideImpersonation = true`.

Dass `example.exists(...)` das Szenario nach dem `val` benennt (`Identity approved`), braucht
Orchescala ab pme123/orchescala#87 (in develop) – vorher hiess es `example`.

`actingAs` und die zweite Test-Identität `testActorIdentityCorrelation` gibt es ab
pme123/orchescala#92 (in develop).

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
Starts ab («bound to process instance …, not to the job's … (root: -)»). Ab pme123/orchescala#96
(in develop, 0.9.0-SNAPSHOT) fragt der C8-Worker die Root-Instanz dann bei der Engine nach
(`GET /v2/process-instances/{key}`) – erneut nur bei einem 404, solange ein eben gestarteter
Teilprozess dort noch fehlt. C7 braucht das nicht.

## Einmalige Ausführung – `democompany-patterns-uniqueExecutionV1`

```
✉ Start ─▶ [Init Process] ─▶ [Antrag bearbeiten] ─▶ (succeeded)

┌ Prozess läuft schon (Ereignis-Subprozess, nicht unterbrechend) ┐
│  ✉ Start-Nachricht nochmals ─▶ (läuft schon: alreadyRunning = true) │
└────────────────────────────────────────────────────────────────┘
```

Derselbe Prozess soll für denselben Fall nur einmal laufen. Er startet deshalb über ein
**Nachrichten-Startereignis** (Name der Nachricht = Prozess-ID) mit einem **`businessKey`**. Solange
er läuft, fängt ein nicht unterbrechender Ereignis-Subprozess **dieselbe Nachricht**: ein zweiter
Start mit demselben `businessKey` landet in der laufenden Instanz (`alreadyRunning = true`) – eine
zweite gibt es nicht.

- Der InitWorker setzt `alreadyRunning = false`; das Ende des Ereignis-Subprozesses setzt es auf `true`.
- Die Nachricht trägt das In mit – ein zweiter Start überschreibt also dessen Variablen in der
  laufenden Instanz.

| | Camunda 7 | Camunda 8 |
|---|---|---|
| Start | `messageEventDefinition` – der `businessKey` ist der der Instanz | ebenso – Orchescala setzt dazu die Variable `businessKey` |
| Ereignis-Subprozess | fängt die Nachricht, weil C7 sie zuerst laufenden Instanzen mit diesem `businessKey` zuordnet | die Nachricht hat `zeebe:subscription correlationKey="=businessKey"` – das Abo öffnet beim Start |
| `alreadyRunning = true` | Execution Listener (`start`) am Ende | Output-Mapping am Ende |
| zweiter Start über Orchescala | wird abgelehnt (409: «correlated to a running process instance») | ebenso – Orchescala schickt den `businessKey` als Korrelationsschlüssel mit (siehe unten) |

| Datei | Inhalt |
|---|---|
| [`UniqueExecution.scala`](01-domain/src/main/scala/democompany/patterns/domain/uniqueExecution/v1/UniqueExecution.scala) | `In`, `InitIn` (`alreadyRunning`), `Out` |
| [`EditRequestUT.scala`](01-domain/src/main/scala/democompany/patterns/domain/uniqueExecution/v1/EditRequestUT.scala) | «Antrag bearbeiten» – solange sie offen ist, läuft der Prozess |
| [`StartAgainME.scala`](01-domain/src/main/scala/democompany/patterns/domain/uniqueExecution/v1/StartAgainME.scala) | die Start-Nachricht ein zweites Mal |
| [`UniqueExecutionWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/uniqueExecution/v1/UniqueExecutionWorker.scala) | InitWorker: Prüfung des In, `alreadyRunning = false` |
| [`camunda/patterns-uniqueExecutionV1.bpmn`](src/main/resources/camunda/patterns-uniqueExecutionV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-uniqueExecutionV1.bpmn`](src/main/resources/camunda8/patterns-uniqueExecutionV1.bpmn) | Camunda 8 |
| [`UniqueExecutionSimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/UniqueExecutionSimulation.scala) | die Szenarien, für beide Engines gleich |

### Simulationen

Beide Szenarien starten mit der Nachricht (`.startWithMsg`); der `businessKey` ist der Name des
Szenarios. Ein Szenario mit ungültigem In gibt es hier nicht (das prüft die Simulation von «Init
Process»): Seine Instanz bliebe mit dem Incident stehen, und die Nachricht des nächsten Laufs
(gleicher `businessKey`) käme dann in ihr an – genau das, was das Pattern tut (409). Direkt starten
lässt sich der Prozess in C8 nicht, er hat dort nur den Nachrichten-Start.

1. **`UniqueExecution started`** – einmal gestartet, «Antrag bearbeiten» erledigt: endet mit
   `alreadyRunning = false`.
2. **`UniqueExecution started twice`** – während «Antrag bearbeiten» offen ist, kommt die
   Start-Nachricht mit demselben `businessKey` nochmals (`StartAgainME.withBusinessKey(...)`): der
   Ereignis-Subprozess fängt sie, der Prozess endet mit `alreadyRunning = true`. Die Nachricht wartet,
   bis der InitWorker `alreadyRunning = false` gesetzt hat (`.waitFor("alreadyRunning", false)`) –
   sonst überschreibt er das `true`.

```bash
./helper.scala deploy UniqueExecutionC7Simulation
```
```bash
./helper.scala deploy UniqueExecutionC8Simulation
```

Bleibt eine Instanz eines Szenarios stehen (ein abgebrochener Lauf, ein zweiter Worker, der den Job
hält), lehnt der nächste Lauf dessen Start mit 409 ab – das Pattern wirkt. Dann die Instanz in
Cockpit bzw. Operate abbrechen.

### Voraussetzung: Orchescala (C8)

Ab Orchescala 0.9.0 ([pme123/orchescala#98](https://github.com/pme123/orchescala/pull/98)) schickt der Start über Orchescala (`POST /process/{messageName}/message?businessKey=…`,
`C8ProcessInstanceService.startProcessByMessage`) den `businessKey` als Korrelationsschlüssel mit:

- **Mit dem Pattern** landet ein zweiter Start in der laufenden Instanz – Orchescala antwortet wie in
  C7 mit 409 und setzt dort keine Identität.
- **Ohne das Pattern** startet ein zweiter Start weiter eine neue Instanz, wie bisher und wie in C7:
  Zeebe lässt pro Korrelationsschlüssel nur eine laufende Instanz eines Nachrichten-Starts zu – lehnt
  es ab, schickt Orchescala die Nachricht nochmals ohne Schlüssel.

Ob die Nachricht eine neue Instanz gestartet hat, sieht Orchescala am Key: eine neue ist jünger als
die Nachricht (auf ihrer Partition), eine laufende älter. Auf c8run 8.9.21 nachgeprüft.

Vorher schickte der C8-Start keinen Schlüssel mit – dann passte das Abo des Ereignis-Subprozesses
nicht, und jeder Start legte eine neue Instanz an.

## Prozess-Status am Ende – `democompany-patterns-processStatusV1`

```
Start ─▶ [Init Process] ─▶ ◇ Antrag bewilligt? ─ ja ──▶ (succeeded)
                                  └─ nein ─▶ (canceled)
```

Jedes Ende setzt **`processStatus`** – so sieht der Aufrufer (und die Simulation), wie der Prozess
ausgegangen ist. Den Wert gibt man beim Einfügen als Parameter `processStatus` an (Vorgabe
`succeeded`); er ist Text, kein Ausdruck. Ein Status von Orchescala (`ProcessStatus`: `succeeded`,
`canceled`, `failed`, `notValid`, `output-mocked`) wird im `Out` ein fester Fall.

Hier entscheidet ein Gateway über `approved` aus dem In: bewilligt endet der Prozess mit
`succeeded`, sonst mit `canceled`.

| | Camunda 7 | Camunda 8 |
|---|---|---|
| `processStatus` am Ende | Execution Listener (`start`): `execution.setVariable("processStatus", "…")` | Output-Mapping `zeebe:output source="=&quot;…&quot;" target="processStatus"` |

Die anderen Beispielprozesse tragen das Pattern an ihrem Ende `succeeded` schon. Das Ende
`output-mocked` setzt `processStatus` auch – es gehört aber zu «Init Process», nicht zu diesem
Pattern: die Orch Spec erkennt dort nur «Init Process».

| Datei | Inhalt |
|---|---|
| [`ProcessStatusExample.scala`](01-domain/src/main/scala/democompany/patterns/domain/processStatus/v1/ProcessStatusExample.scala) | `In` (`approved`), `Out` |
| [`ProcessStatusExampleWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/processStatus/v1/ProcessStatusExampleWorker.scala) | InitWorker: Prüfung des In |
| [`camunda/patterns-processStatusV1.bpmn`](src/main/resources/camunda/patterns-processStatusV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-processStatusV1.bpmn`](src/main/resources/camunda8/patterns-processStatusV1.bpmn) | Camunda 8 |
| [`ProcessStatusSimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/ProcessStatusSimulation.scala) | die Szenarien, für beide Engines gleich |

Das Objekt heisst `ProcessStatusExample`, nicht `ProcessStatus` – sonst verdeckte es den Typ
`ProcessStatus` von Orchescala.

### Simulationen

1. **`ProcessStatus succeeded`** – `approved = true`: endet mit `processStatus = succeeded`.
2. **`ProcessStatus canceled`** – `approved = false`: endet mit `processStatus = canceled`.
3. **`ProcessStatus mocked`** – endet über den Pfad `output-mocked` von «Init Process».

Ein Szenario mit ungültigem In gibt es hier nicht – das prüft die Simulation von «Init Process».

```bash
./helper.scala deploy ProcessStatusC7Simulation
```
```bash
./helper.scala deploy ProcessStatusC8Simulation
```

## Prozess mit Fehler stoppen – `democompany-patterns-stopWithErrorV1`

```
                                                      ┌─ ja ──▶ ○ Limit überschritten ⚡ Incident ─┐
Start ─▶ [Init Process] ─▶ ◇ Kunde gesperrt? ─ nein ─▶ ◇ Limit überschritten? ─ nein ─────────────▶ (succeeded)
                                  └─ ja ──▶ (Kunde gesperrt) ⚡ Incident
```

Das Pattern **unterbricht den Prozess mit einem Fehler, den DevOps im Cockpit (C7) bzw. in Operate (C8)
behandeln**: der Prozess bleibt sofort mit einem **Incident** stehen, dessen Meldung das Problem
beschreibt. Das Ereignis ist **orange** – ohne Blitz: ein Fehler-Endereignis würfe in C8 den BPMN-Fehler,
ohne die Mappings auszuwerten (der Incident hiesse dann nur «… not caught»), und ein Aufrufer könnte ihn
fangen. Es passt an ein End-Ereignis oder an ein werfendes Zwischenereignis; die Meldung
(`message`) ist Text oder FEEL – hier `= "Kunde " + customerName + " ist gesperrt."` am Ende und
`= "Limit von " + customerName + " überschritten."` am Zwischenereignis.

**Ende oder Zwischenereignis:** am Ende ist der Prozess fertig, wenn DevOps ihn behandelt haben (meist
abbrechen). Am Zwischenereignis kann er weiterlaufen: nachdem die Ursache behoben ist, schieben DevOps
die Instanz per Modification hinter das Ereignis – hier geht sie dann zu `succeeded`.

**Behandeln:** ein Retry wirft denselben Fehler wieder – der Incident ist kein vorübergehender
Fehler, sondern eine Stelle, an der jemand entscheiden muss. DevOps brechen die Instanz ab oder schieben
sie per Process Instance Modification weiter (C7: Cockpit, C8: Operate), nachdem die Ursache behoben ist.

| | Camunda 7 | Camunda 8 |
|---|---|---|
| Meldung | `camunda:inputParameter _errorMessage` – FEEL wird JUEL (`${"Kunde ".concat(customerName)…}`) | Output-Mapping `_errorMessage` |
| stoppen | Groovy-Listener (`end`): `throw new IllegalArgumentException(…)`; `asyncBefore`, damit der Incident am Ereignis entsteht, und `failedJobRetryTimeCycle` `R1/PT0S` – ein Versuch | Output-Mapping `_stopped = assert(false, false, string(_errorMessage))` |
| Incident | Meldung der Exception | `IO_MAPPING_ERROR` – «Assertion failure …: Kunde Peter Muster ist gesperrt.» |

**C7:** die Exception kommt im Job von `asyncBefore`. `failedJobRetryTimeCycle` `R1/PT0S` lässt ihn nur
einmal laufen – der Incident entsteht sofort, gleich wie die Engine Retries sonst einstellt.
`asyncBefore` ist eine Transaktionsgrenze am Ereignis.

**C8:** Zeebe fasst die Output-Mappings zu einem FEEL-Kontext zusammen (`{_errorMessage: …, _stopped:
…}`) – ein späterer Eintrag sieht die früheren. Ein Ziel zweimal gäbe es darin nur einmal; deshalb
steht `assert` unter einem eigenen Namen. `assert` ist auf c8run 8.9 nachgeprüft. Wird der Incident
aufgelöst, wertet Zeebe das Mapping neu aus – der Prozess bleibt stehen, wie in C7. Fester Text mit
`"` geht als C8-Meldung nicht (er stünde in einer FEEL-Zeichenkette) – dann als FEEL schreiben.

| Datei | Inhalt |
|---|---|
| [`StopWithError.scala`](01-domain/src/main/scala/democompany/patterns/domain/stopWithError/v1/StopWithError.scala) | `In` (`blocked`, `limitExceeded`), `Out` |
| [`StopWithErrorWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/stopWithError/v1/StopWithErrorWorker.scala) | InitWorker: Prüfung des In |
| [`camunda/patterns-stopWithErrorV1.bpmn`](src/main/resources/camunda/patterns-stopWithErrorV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-stopWithErrorV1.bpmn`](src/main/resources/camunda8/patterns-stopWithErrorV1.bpmn) | Camunda 8 |
| [`StopWithErrorSimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/StopWithErrorSimulation.scala) | die Szenarien, für beide Engines gleich |

### Simulationen

1. **`StopWithError succeeded`** – `blocked = false`: endet mit `processStatus = succeeded`.
2. **`StopWithError blocked`** – `blocked = true`: Incident am Ende mit «Kunde Peter Muster ist
   gesperrt.» (`incidentScenario`).
3. **`StopWithError limit exceeded`** – `limitExceeded = true`: Incident am Zwischenereignis mit «Limit
   von Peter Muster überschritten.».
4. **`StopWithError mocked`** – endet über den Pfad `output-mocked` von «Init Process».

```bash
./helper.scala deploy StopWithErrorC7Simulation
```
```bash
./helper.scala deploy StopWithErrorC8Simulation
```

Bleiben die Instanzen der Szenarien 2 und 3 stehen, ist das gewollt – in Cockpit bzw. Operate abbrechen
oder (Szenario 3) per Modification weiterschieben.

## Prozess-Event senden – `democompany-patterns-sendProcessEventV1`

```
Start ─▶ [Init Process] ─▶ [Prozess-Event: gestartet] ─▶ [Prozess-Event: beendet] ─▶ (succeeded)
                                  │ ruft                        │ ruft
                                  ▼                             ▼
                 democompany-patterns-processEventV1:  (Event) ─▶ [Event melden] ─▶ (gemeldet)
                                                                       │
                                                                       ▼  Service PostEvent
```

Das Pattern **meldet einen Schritt des Prozesses an einen eigenen Event-Prozess** – hier zu Beginn
`started` und am Ende `finished`. Es sitzt an einer **Call Activity**; welchen Prozess sie ruft, wählt
man im Pattern (`eventProcess`, Auswahl aus den Prozessen des Katalogs – ohne Wahl zeigt die Orch Spec
einen Hinweis). Der Event-Prozess bekommt:

| Eingabe | Inhalt |
|---|---|
| `eventKey` | was passiert ist – Parameter `eventKey`, Text oder FEEL (Vorgabe `started`) |
| `processDefinitionKey` | die ID des meldenden Prozesses |
| `processInstanceId` | seine Instanz |

Zurück gibt er nichts. Was er mit dem Event macht, ist seine Sache – **das ist der Teil, den jede Firma
selbst festlegt**: Der meldende Prozess kennt nur das Pattern und den Event-Prozess; ob das Event an ein
Fremdsystem, ein Monitoring oder in mehrere Schritte geht, ändert man im Event-Prozess, ohne die
meldenden Prozesse anzufassen.

Hier (`democompany-patterns-processEventV1`) gibt **«Event melden»** das Event an den Service
**`PostEvent`** aus **democompany-services** weiter (Topic `democompany-services-eventsV1-PostEvent`,
`POST events` mit `eventKey`, `processDefinitionKey`, `processInstanceId`) und endet mit `succeeded`.
Seinen ServiceWorker führt die Worker-App dieses Projekts mit (wie «Kunde laden» bei «Mocks und
Identität weitergeben»).

| | Camunda 7 | Camunda 8 |
|---|---|---|
| gerufener Prozess | `calledElement` | `zeebe:calledElement processId`, `propagateAllParentVariables` und `propagateAllChildVariables` aus |
| Prozess-ID | `${execution.getProcessDefinition().getKey()}` | die ID als Text (`="democompany-patterns-sendProcessEventV1"`) |
| Instanz | `${execution.getProcessInstanceId()}` | `=string(camunda.processInstance.key)` – ab Camunda 8.10, ohne «Init Process» |

Damit der Service im Test gemockt ist, tragen beide Call Activities zusätzlich **«Mocks und Identität
weitergeben»** (`_servicesMocked`, `_mockedWorkers`, `_identityCorrelation`) – die Orch Spec erkennt
beide Patterns an derselben Call Activity.

| Datei | Inhalt |
|---|---|
| [`SendProcessEvent.scala`](01-domain/src/main/scala/democompany/patterns/domain/sendProcessEvent/v1/SendProcessEvent.scala) | `In`, `Out` |
| [`ProcessEvent.scala`](01-domain/src/main/scala/democompany/patterns/domain/processEvent/v1/ProcessEvent.scala) | der Event-Prozess: `In` (`eventKey`, `processDefinitionKey`, `processInstanceId`), `Out` |
| [`PostEvent.scala`](../democompany-services/01-domain/src/main/scala/democompany/services/domain/events/v1/PostEvent.scala) | der Service «Event melden» (democompany-services) |
| [`SendProcessEventWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/sendProcessEvent/v1/SendProcessEventWorker.scala) | InitWorker: Prüfung des In |
| [`camunda/patterns-sendProcessEventV1.bpmn`](src/main/resources/camunda/patterns-sendProcessEventV1.bpmn), [`camunda/patterns-processEventV1.bpmn`](src/main/resources/camunda/patterns-processEventV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-sendProcessEventV1.bpmn`](src/main/resources/camunda8/patterns-sendProcessEventV1.bpmn), [`camunda8/patterns-processEventV1.bpmn`](src/main/resources/camunda8/patterns-processEventV1.bpmn) | Camunda 8 |
| [`SendProcessEventSimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/SendProcessEventSimulation.scala) | die Szenarien, für beide Engines gleich |

### Simulationen

1. **`SendProcessEvent succeeded`** – `.mockServices`: meldet `started` und `finished`, «Event melden»
   ist im Event-Prozess gemockt; endet mit `processStatus = succeeded`. Fehlte der Event-Prozess, bliebe
   der Prozess mit einem Incident an der Call Activity stehen.
2. **`SendProcessEvent mocked`** – endet über den Pfad `output-mocked` von «Init Process», ohne Event.
3. **`ProcessEvent reported`** – der Event-Prozess für sich, `.mockServices`: meldet das Event und
   endet mit `succeeded`.

```bash
./helper.scala deploy SendProcessEventC7Simulation
```
```bash
./helper.scala deploy SendProcessEventC8Simulation
```

## Abbruch per Signal – `democompany-patterns-cancelBySignalV1`

```
Start ─▶ [Init Process] ─▶ [Antrag bearbeiten] ─▶ (succeeded)

┌ Abbruch (Ereignis-Subprozess, unterbrechend) ──────────┐
│ (Signal cancel-<Instanz>) ─▶ (canceled)                 │
└─────────────────────────────────────────────────────────┘
```

Das Pattern **bricht eine Instanz von aussen ab**: ein unterbrechender Ereignis-Subprozess wartet auf das
Signal `cancel-<Instanz>`. Kommt es, beendet er den Prozess – egal, wo er gerade wartet – und setzt
`processStatus = canceled`. Ohne Signal wird «Antrag bearbeiten» erledigt, und der Prozess endet mit
`succeeded`. Das Pattern sitzt am **Prozess**; das Präfix des Signals ist ein Parameter
(`signalPrefix`, Vorgabe `cancel-`).

Das Signal ist an **diese eine Instanz** gebunden – sein Name wird beim Start der Instanz gebildet:

| | Camunda 7 | Camunda 8 (ab 8.10) |
|---|---|---|
| Signalname | `cancel-${execution.processInstanceId}` | `="cancel-" + string(camunda.processInstance.key)` |
| Status | Listener am Ende `canceled` | Output-Mapping am Ende `canceled` |

Senden lässt es sich über Orchescala mit dem Platzhalter `{processInstanceId}` im Namen
(`cancel-{processInstanceId}`, siehe [`CancelSE.scala`](01-domain/src/main/scala/democompany/patterns/domain/cancelBySignal/v1/CancelSE.scala)),
oder aus einem anderen Element des Prozesses mit einem Signal-Ende bzw. -Zwischenereignis desselben Namens.

**Camunda 8 braucht 8.10.** Erst ab 8.10 kennt FEEL `camunda.processInstance.key` – auf 8.9 bleibt jede
Instanz schon beim Start mit einem Incident stehen
(«Expected result of the expression … to be 'STRING', but was 'NULL'»; auf c8run 8.9.21 nachgeprüft).
Die Simulationen für C8 laufen auf c8run 8.10.8.

| Datei | Inhalt |
|---|---|
| [`CancelBySignal.scala`](01-domain/src/main/scala/democompany/patterns/domain/cancelBySignal/v1/CancelBySignal.scala) | `In`, `InitIn` (`waitForSignal`), `Out` |
| [`EditRequestUT.scala`](01-domain/src/main/scala/democompany/patterns/domain/cancelBySignal/v1/EditRequestUT.scala) | «Antrag bearbeiten» – hier wartet der Prozess |
| [`CancelSE.scala`](01-domain/src/main/scala/democompany/patterns/domain/cancelBySignal/v1/CancelSE.scala) | das Signal `cancel-{processInstanceId}` |
| [`CancelBySignalWorker.scala`](03-worker/src/main/scala/democompany/patterns/worker/cancelBySignal/v1/CancelBySignalWorker.scala) | InitWorker: Prüfung des In, `waitForSignal = true` |
| [`camunda/patterns-cancelBySignalV1.bpmn`](src/main/resources/camunda/patterns-cancelBySignalV1.bpmn) | Camunda 7 |
| [`camunda8/patterns-cancelBySignalV1.bpmn`](src/main/resources/camunda8/patterns-cancelBySignalV1.bpmn) | Camunda 8 (ab 8.10) |
| [`CancelBySignalSimulation.scala`](03-simulation/src/test/scala/democompany/patterns/simulation/CancelBySignalSimulation.scala) | die Szenarien, für beide Engines gleich |

### Simulationen

1. **`CancelBySignal succeeded`** – kein Signal: «Antrag bearbeiten» wird erledigt, der Prozess endet mit
   `processStatus = succeeded`.
2. **`CancelBySignal canceled`** – das Signal kommt, während der Prozess an «Antrag bearbeiten» wartet:
   der Ereignis-Subprozess bricht ab, der Prozess endet mit `processStatus = canceled`. Die Simulation
   schickt das Signal erst, wenn der InitWorker durch ist (`waitForSignal = true`).
3. **`CancelBySignal mocked`** – endet über den Pfad `output-mocked` von «Init Process».

Wie die anderen Pattern-Simulationen startet sie ohne Prüfung über die Worker-App
(`validateProcess(false)`) – das In prüft der InitWorker in der Engine.

```bash
./helper.scala deploy CancelBySignalC7Simulation
```
```bash
./helper.scala deploy CancelBySignalC8Simulation
```
