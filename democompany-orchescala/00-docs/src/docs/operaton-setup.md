# Operaton Integration

This project supports [Operaton](https://operaton.org/), an open-source BPM platform that is API-compatible with Camunda 7. Operaton can be used alongside or as an alternative to Camunda 7 and Camunda 8.

## What is Operaton?

Operaton is a community-driven fork of Camunda 7 that maintains API compatibility while providing:
- Open-source BPM engine
- REST API compatible with Camunda 7
- Cockpit for process monitoring
- Support for BPMN 2.0 processes

## Environment Variables

To connect to an Operaton instance, configure the following environment variables:

### Required Variables

```bash
# Operaton REST API endpoint
OPERATON_BASE_URL=http://localhost:9999/engine-rest

# Operaton Cockpit URL (for monitoring)
OPERATON_COCKPIT_URL=http://localhost:9999/camunda/app/cockpit/default/
```

### Optional OAuth Variables (if using authentication)

```bash
# SSO/OAuth configuration (similar to Camunda 7)
SSO_BASE_URL=http://your-sso-server:8090/auth
SSO_REALM=your-realm
SSO_CLIENT_NAME=your-client
SSO_CLIENT_SECRET=your-secret
SSO_SCOPE=your-scope
SSO_TECHUSER_NAME=admin
SSO_TECHUSER_PASSWORD=admin
```

## Running Operaton Locally

### Using Docker

The easiest way to run Operaton locally is with Docker:

```bash
docker run -d --name operaton \
  -p 9999:8080 \
  operaton/operaton:latest
```

Access the Cockpit at: `http://localhost:9999/camunda/app/cockpit/default/`

Default credentials: `demo` / `demo`

### Using Docker Compose

This project ships a Compose file with a Postgres database - _docker/operaton/docker-compose.yml_.

Start it with:

```bash
cd democompany-orchescala/docker/operaton
docker-compose up -d
```

It maps the engine to port _9999_, as _8080_ is taken by Camunda 7.

## Using Operaton in Your Code

### Simulations

Create an Operaton-specific simulation by extending `CompanyOperatonSimulation`:

```scala
package democompany.cards.simulation

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*
import orchescala.engine.domain.EngineType

class OrderCreditcardOperatonSimulation 
  extends OrderCreditcardSimulation, CompanyOperatonSimulation:
  
  protected def engineType: EngineType = EngineType.Op
```

### Running Simulations

```bash
# Deploy and run Operaton simulation
./helper.scala deploy OrderCreditcardOperatonSimulation

# Run simulation tests
sbt "simulation/testOnly *OrderCreditcardOperatonSimulation"
```

### Multi-Engine Support (G-Simulation)

Use `CompanyGSimulation` to support multiple engines (C7, C8, and Operaton) in a single simulation:

```scala
class OrderCreditcardGSimulation 
  extends OrderCreditcardSimulation, CompanyGSimulation:
  
  // Automatically supports all three engines
  // Default engine is the first in the list (C7)
```

The G-Simulation provides cockpit URLs for all engines:
- Camunda 7: `CAMUNDA_COCKPIT_URL`
- Camunda 8: `CAMUNDA8_CLOUD_OPERATE_URL`
- Operaton: `OPERATON_COCKPIT_URL`

## Workers

Operaton workers follow the same pattern as Camunda 7 workers since the APIs are compatible.

### Creating an Operaton Worker

```scala
package democompany.cards.worker.orderCreditcard.v1

import democompany.cards.domain.orderCreditcard.v1.OrderCreditcard.*

class OrderCreditcardWorker 
  extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:
  
  lazy val inOutExample = example
  
  override def customInit(in: In): InitIn =
    InitIn(
      initCreditCardAccount = in.creditCardAccount.copy(accountId = None),
      simpleValue = in.mainCardHolder.map(_.cards.map(_.embossedLineOne).mkString(","))
    )
```

### Running Workers

```bash
# Start workers (supports all engines including Operaton)
sbt worker/run
```

Workers automatically connect to the configured engine based on environment variables.

## Deployment

### Deploy BPMN Processes

Processes are deployed using the simulation helper:

```bash
# Deploy to Operaton
./helper.scala deploy OrderCreditcardOperatonSimulation
```

### Verify Deployment

1. Open Operaton Cockpit: `http://localhost:9999/camunda/app/cockpit/default/`
2. Navigate to "Processes"
3. Verify your process definition appears

## Troubleshooting

### Connection Issues

If you can't connect to Operaton:

1. Verify Operaton is running: `curl http://localhost:9999/engine-rest/engine`
2. Check environment variables are set correctly
3. Verify the REST API endpoint is accessible

### Process Deployment Fails

1. Check BPMN file is valid
2. Verify Operaton version compatibility
3. Check Operaton logs for errors

### Worker Not Picking Up Tasks

1. Verify worker is running
2. Check topic names match between BPMN and worker code
3. Verify Operaton REST API is accessible from worker

## Differences from Camunda 7

While Operaton is API-compatible with Camunda 7, there may be minor differences:

- **Version**: Operaton is based on Camunda 7.x but may have different version numbers
- **Features**: Some Camunda enterprise features may not be available
- **Community**: Operaton is community-driven and open-source

## Next Steps

- [Process Patterns](pattern.html) - Learn about process design patterns
- [Instructions](development/instructions.html) - Release and deployment instructions
- [Orchescala Documentation](https://pme123.github.io/orchescala/) - Full library documentation

