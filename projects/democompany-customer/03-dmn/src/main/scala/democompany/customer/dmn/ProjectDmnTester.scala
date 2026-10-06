package democompany.customer
package dmn

// Runs the DMN Tester of this project:
//   dmn/runMain democompany.customer.dmn.ProjectDmnTester
// It writes the configurations, starts the tester on
// http://localhost:8883 and keeps it running until you stop it (Ctrl-C).
object ProjectDmnTester extends CompanyDmnTester:

  override protected def dmnTesterObjects = Seq(
    // myDmn
  )
  /* example - `.from("c8")` picks a named DMN source of your company
     configuration and writes the config into `dmnConfigs/c8`:
  private lazy val myDmn =
    import myProcess.v1.*

    MyDmn.example
      .testUnit
      .testValues(
        _.value,
        1,
        2
      )
      .testValues(
        _.age,
        64,
        65,
        66
      )
  */

end ProjectDmnTester