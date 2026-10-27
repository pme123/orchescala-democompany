package democompany.patterns
package api

object ApiProjectCreator extends CompanyApiCreator:

  val title = "Patterns"

  lazy val projectDescr =
    "Katalog mit Beispielprozessen für die Orchescala-Patterns - je Pattern ein Prozess mit Simulationen für Camunda 7 und Camunda 8."

  val version = "0.1.0-SNAPSHOT"

  document(
    initProcessApi,
    identityApi
  )

  private lazy val initProcessApi =
    import democompany.patterns.domain.initProcess.v1.*
    api(InitProcess.example)()

  private lazy val identityApi =
    import democompany.patterns.domain.identity.v1.*
    api(Identity.example)(
      ApproveRequestUT.example,
      CloseRequestUT.example
    )
end ApiProjectCreator
