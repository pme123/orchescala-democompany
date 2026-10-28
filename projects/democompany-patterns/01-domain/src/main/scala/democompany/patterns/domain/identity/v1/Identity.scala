package democompany.patterns
package domain.identity.v1

/** Example process for the patterns «Identität übernehmen» and «Identität zurücksetzen».
  *
  * The gateway sets `_identityCorrelation` only at the start. Completing a user task adds the
  * signed identity of the person who did it as `_identityCorrelationOfActor` - the event
  * «Identität übernehmen» right after the task takes it over (and keeps the one before in
  * `_identityCorrelationBefore`), the event «Identität zurücksetzen» sets the one before again.
  */
object Identity extends CompanyBpmnProcessDsl:

  val processName   = "democompany-patterns-identityV1"
  val descr: String =
    """Beispiel für die Patterns «Identität übernehmen» und «Identität zurücksetzen».
      |Nach der Benutzeraufgabe übernimmt der Prozess die Identität der Person, die sie erledigt hat
      |(`_identityCorrelation = _identityCorrelationOfActor`, die bisherige in `_identityCorrelationBefore`) -
      |«Identität zurücksetzen» setzt danach wieder die Identität vom Start.
      |""".stripMargin

  case class In(
      @description("Name des Kunden - darf nicht leer sein (sonst meldet der InitWorker `validation-failed`).")
      customerName: String
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(customerName = "Peter Muster")
  end In

  case class Out(
      processStatus: ProcessStatus,
      customerName: String,
      @description("Antrag freigegeben - aus der Benutzeraufgabe «Antrag freigeben».")
      approved: Boolean,
      @description("Die Benutzernamen, mit denen «Identität prüfen» lief - nach der Übernahme der Freigebende, nach dem Zurücksetzen wieder der Starter.")
      checkedUsernames: Seq[String]
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out(
      processStatus = ProcessStatus.succeeded,
      customerName = In.example.customerName,
      approved = ApproveRequestUT.Out.example.approved,
      checkedUsernames = Seq("actor", "admin")
    )
  end Out

  lazy val example = process(
    In.example,
    Out.example
  )
end Identity
