# Process Pattern
We try to establish Patterns for doing the same tasks.
This documentation lists them and gives you some examples.

## Supported BPM Engines

This project supports three BPM engines:

### Camunda 7 (C7)
- Traditional Camunda Platform
- REST API based
- OAuth2 authentication support
- Cockpit for monitoring

### Camunda 8 (C8)
- Cloud-native Camunda Platform
- Zeebe workflow engine
- gRPC and REST APIs
- Operate for monitoring

### Operaton (Op)
- Open-source BPM platform
- API-compatible with Camunda 7
- Community-driven
- Cockpit for monitoring

## Engine Selection Patterns

### Single Engine Simulation

Use engine-specific simulation traits when you want to target a specific engine:

```scala
// Camunda 7
class MyProcessC7Simulation extends MyProcessSimulation, CompanyC7Simulation

// Camunda 8
class MyProcessC8Simulation extends MyProcessSimulation, CompanyC8Simulation

// Operaton
class MyProcessOperatonSimulation extends MyProcessSimulation, CompanyOperatonSimulation
```

### Multi-Engine Simulation (G-Simulation)

Use `CompanyGSimulation` when you want to support all engines:

```scala
class MyProcessGSimulation extends MyProcessSimulation, CompanyGSimulation
```

Benefits:
- Single simulation supports C7, C8, and Operaton
- Automatically provides cockpit URLs for all engines
- Default engine is C7 (first in the list)
- Easy to switch between engines for testing

## Worker Patterns

Workers are engine-agnostic and work with all supported engines:

```scala
class MyWorker extends CompanyInitWorkerDsl[In, Out, InitIn, InConfig]:
  lazy val inOutExample = example

  override def customInit(in: In): InitIn =
    // Your initialization logic
```

The worker framework automatically:
- Connects to the configured engine
- Handles engine-specific API differences
- Manages authentication and authorization

## Configuration Patterns

### Environment-Based Configuration

Each engine uses environment variables for configuration:

**Camunda 7:**
```bash
CAMUNDA_BASE_URL=http://localhost:8080/engine-rest
CAMUNDA_COCKPIT_URL=http://localhost:8080/camunda/app/cockpit/default/
```

**Camunda 8:**
```bash
CAMUNDA8_CLOUD_GRPC_URL=your-grpc-url
CAMUNDA8_CLOUD_REST_URL=your-rest-url
CAMUNDA8_CLOUD_OPERATE_URL=your-operate-url
CAMUNDA8_CLOUD_CLIENTID=your-client-id
CAMUNDA8_CLOUD_CLIENTSECRET=your-client-secret
```

**Operaton:**
```bash
OPERATON_BASE_URL=http://localhost:8080/engine-rest
OPERATON_COCKPIT_URL=http://localhost:8080/camunda/app/cockpit/default/
```

### OAuth Configuration

For engines requiring OAuth (C7, Operaton):

```bash
SSO_BASE_URL=http://your-sso-server:8090/auth
SSO_REALM=your-realm
SSO_CLIENT_NAME=your-client
SSO_CLIENT_SECRET=your-secret
SSO_TECHUSER_NAME=admin
SSO_TECHUSER_PASSWORD=admin
```

## Deployment Patterns

### Deploy to Specific Engine

```bash
# Deploy to Camunda 7
./helper.scala deploy MyProcessC7Simulation

# Deploy to Camunda 8
./helper.scala deploy MyProcessC8Simulation

# Deploy to Operaton
./helper.scala deploy MyProcessOperatonSimulation
```

### Run Simulations

```bash
# Run specific engine simulation
sbt "simulation/testOnly *MyProcessC7Simulation"
sbt "simulation/testOnly *MyProcessC8Simulation"
sbt "simulation/testOnly *MyProcessOperatonSimulation"

# Run all simulations
sbt simulation/test
```

## Best Practices

1. **Use G-Simulation for flexibility**: Start with `CompanyGSimulation` to support all engines
2. **Environment-specific configs**: Use different environment variables for dev/test/prod
3. **Engine-agnostic workers**: Write workers that work with all engines
4. **Test across engines**: Run simulations on all supported engines before release
5. **Document engine requirements**: Clearly specify which engine(s) a process requires

## Migration Patterns

### From Camunda 7 to Operaton

Since Operaton is API-compatible with Camunda 7:

1. Change simulation trait from `CompanyC7Simulation` to `CompanyOperatonSimulation`
2. Update environment variables (CAMUNDA_* → OPERATON_*)
3. Deploy to Operaton instance
4. No code changes required for workers or process logic

### From Camunda 7 to Camunda 8

Requires more changes due to different APIs:

1. Change simulation trait from `CompanyC7Simulation` to `CompanyC8Simulation`
2. Update environment variables for C8 cloud
3. Review BPMN for C8 compatibility (some features differ)
4. Test thoroughly as APIs are different

## Additional Resources

- [Operaton Setup](operaton-setup.html) - Detailed Operaton configuration
- [Orchescala Documentation](https://pme123.github.io/orchescala/) - Full library documentation
