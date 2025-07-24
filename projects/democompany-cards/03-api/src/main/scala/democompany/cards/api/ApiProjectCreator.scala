package democompany.cards
package api

object ApiProjectCreator extends CompanyApiCreator:

  val title = "democompany-cards"

  lazy val projectDescr =
    "TODO Your Project description."

  val version = "0.1.0-SNAPSHOT"

  document(
    //myProcessApi,
    //..
  )

  /* example:
  private lazy val myProcessApi =
    import myProcess.v1.*
    api(MyProcess.example)(
      // userTasks / workers etc.
    )
  */
end ApiProjectCreator
