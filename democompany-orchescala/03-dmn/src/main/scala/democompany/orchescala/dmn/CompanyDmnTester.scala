package democompany.orchescala.dmn

trait CompanyDmnTester extends DmnTesterConfigCreator:

  override def starterConfig: DmnTesterStarterConfig =
    DmnTesterStarterConfig(companyName = "democompany")

end CompanyDmnTester
