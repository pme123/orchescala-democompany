package democompany.patterns.simulation

// ./helper.scala deploy CancelBySignalC8Simulation
// simulation/test
// simulation/testOnly *CancelBySignalC8Simulation
// needs Camunda 8.10 - the signal name uses `camunda.processInstance.key`
class CancelBySignalC8Simulation extends CancelBySignalSimulation, CompanyC8Simulation
