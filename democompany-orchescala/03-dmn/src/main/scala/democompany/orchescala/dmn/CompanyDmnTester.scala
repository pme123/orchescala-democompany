package democompany.orchescala.dmn

import orchescala.dmntester.*

trait CompanyDmnTester extends DmnTesterApp:

  override protected def starterConfig: DmnTesterStarterConfig =
    DmnTesterStarterConfig(companyName = "democompany")
    // Where the DMNs of a project are - name the sources if your
    // projects have DMNs of more than one platform. A decision is then
    // looked up in every source and tested against all of them:
    //
    // DmnTesterStarterConfig(
    //   companyName = "democompany",
    //   dmnSources = Seq(
    //     DmnSource("c7", projectBasePath / "src" / "main" / "resources" / "camunda"),
    //     DmnSource("c8", projectBasePath / "c8" / "src" / "main" / "resources")
    //   )
    // )

end CompanyDmnTester
