# DemoCompany Orchescala

Business Process Management (BPM) integration for DemoCompany using the [Orchescala](https://github.com/pme123/orchescala) library.

## Supported BPM Engines

This project supports three BPM engines:

- **Camunda 7 (C7)**: Traditional Camunda Platform with REST API
- **Camunda 8 (C8)**: Cloud-native Camunda Platform with Zeebe
- **Operaton (Op)**: Open-source BPM platform, API-compatible with Camunda 7

## Quick Start

### Prerequisites

- Scala 3.7.1+
- SBT 1.10.8+
- Java 21+
- Docker (for running BPM engines locally)

### Running Operaton Locally

```bash
docker run -d --name operaton \
  -p 8080:8080 \
  operaton/operaton:latest
```

Access Cockpit: http://localhost:8080/camunda/app/cockpit/default/

### Environment Configuration

Create a `.env` file or set environment variables:

```bash
# Operaton
OPERATON_BASE_URL=http://localhost:8080/engine-rest
OPERATON_COCKPIT_URL=http://localhost:8080/camunda/app/cockpit/default/

# Camunda 7
CAMUNDA_BASE_URL=http://localhost:8080/engine-rest
CAMUNDA_COCKPIT_URL=http://localhost:8080/camunda/app/cockpit/default/

# Camunda 8 (if using)
CAMUNDA8_CLOUD_GRPC_URL=your-grpc-url
CAMUNDA8_CLOUD_REST_URL=your-rest-url
CAMUNDA8_CLOUD_OPERATE_URL=your-operate-url
CAMUNDA8_CLOUD_CLIENTID=your-client-id
CAMUNDA8_CLOUD_CLIENTSECRET=your-client-secret
```

### Build and Test

```bash
# Compile the project
sbt compile

# Run simulations
sbt simulation/test

# Run specific simulation
sbt "simulation/testOnly *OrderCreditcardOperatonSimulation"

# Start workers
sbt worker/run
```

## Project Structure

```
democompany-orchescala/
├── 00-docs/              # Documentation
│   └── src/docs/
│       ├── operaton-setup.md    # Operaton setup guide
│       ├── pattern.md           # Process patterns
│       └── instructions.md      # Release instructions
├── 01-domain/            # Domain models
├── 02-engine/            # Engine configurations
│   └── src/main/scala/democompany/orchescala/engine/
│       ├── CompanyEngineC7Config.scala
│       ├── CompanyEngineC8Config.scala
│       └── CompanyEngineOperatonConfig.scala
├── 03-simulation/        # Process simulations
│   └── src/main/scala/democompany/orchescala/simulation/
│       ├── CompanyC7Simulation.scala
│       ├── CompanyC8Simulation.scala
│       ├── CompanyOperatonSimulation.scala
│       └── CompanyGSimulation.scala  # Multi-engine support
├── 03-worker/            # External task workers
├── 03-api/               # REST API integration
└── projects/             # Business process projects
    ├── democompany-cards/      # Credit card processes
    └── democompany-services/   # Service processes
```

## Creating a Process

### 1. Define Domain Model

Create your process domain in `projects/your-project/01-domain/`:

```scala
package democompany.yourproject.domain.yourprocess.v1

object YourProcess:
  case class In(/* input fields */)
  case class Out(/* output fields */)
  
  val example = In(/* example data */)
```

### 2. Create Simulation

Create simulations for each engine in `projects/your-project/03-simulation/`:

```scala
// Operaton simulation
class YourProcessOperatonSimulation 
  extends YourProcessSimulation, CompanyOperatonSimulation:
  protected def engineType: EngineType = EngineType.Op

// Multi-engine simulation
class YourProcessGSimulation 
  extends YourProcessSimulation, CompanyGSimulation
```

### 3. Implement Workers

Create workers in `projects/your-project/03-worker/`:

```scala
class YourProcessWorker 
  extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:
  
  lazy val inOutExample = example
  
  override def customInit(in: In): InitIn =
    // Your initialization logic
```

### 4. Deploy and Test

```bash
# Deploy to Operaton
./helper.scala deploy YourProcessOperatonSimulation

# Run simulation
sbt "simulation/testOnly *YourProcessOperatonSimulation"
```

## Documentation

Full documentation is available in the `00-docs/` directory:

- [Operaton Setup Guide](00-docs/src/docs/operaton-setup.md) - Detailed Operaton configuration
- [Process Patterns](00-docs/src/docs/pattern.md) - Design patterns and best practices
- [Release Instructions](00-docs/src/docs/instructions.md) - How to create releases

## Examples

See the `projects/` directory for complete examples:

- **democompany-cards**: Credit card order process with C7, C8, and Operaton simulations
- **democompany-services**: Service integration examples

## Dependencies

- [Orchescala](https://github.com/pme123/orchescala) 0.4.0-SNAPSHOT - BPM integration library
- Scala 3.7.1
- ZIO - Functional effects library
- Circe - JSON library

## License

[Your License Here]

## Contact

- Business: [Peter Blank](mailto:peter.blank@todo.ch)
- Technical: [Maya Blue](mailto:maya.blue@todo.ch)

