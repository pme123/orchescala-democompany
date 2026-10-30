package democompany.patterns
package domain.identity.v1

/** Shows which identity the process has at that point: it adds the username of
  * `_identityCorrelation` to `checkedUsernames` - after «Identität übernehmen» it is the copied
  * actor, after «Identität zurücksetzen» the one of the start.
  *
  * A CustomTask: a worker of this process that the Orch Spec recognizes. Unlike a ServiceWorker it
  * does not verify the signature of `_identityCorrelation`. The BPMN maps the username in.
  */
object CheckIdentity extends CompanyBpmnCustomTaskDsl:

  val topicName     = "democompany-patterns-identityV1-CheckIdentity"
  val descr: String =
    "Identität prüfen - hängt den Benutzernamen der Identität des Prozesses an `checkedUsernames` an."

  case class In(
      customerName: String,
      @description("Benutzername der Identität des Prozesses - das BPMN mappt ihn aus `_identityCorrelation`.")
      identityUsername: String,
      @description("Die Benutzernamen der bisherigen Prüfungen.")
      checkedUsernames: Option[Seq[String]] = None
  )
  object In:
    given ApiSchema[In]  = deriveApiSchema
    given InOutCodec[In] = deriveInOutCodec
    lazy val example     = In(
      customerName = Identity.In.example.customerName,
      identityUsername = "actor"
    )
  end In

  case class Out(
      @description("Die Identität des Prozesses wurde geprüft.")
      identityChecked: Boolean = true,
      @description("Die Benutzernamen aller Prüfungen bis hier, in ihrer Reihenfolge.")
      checkedUsernames: Seq[String] = Seq("actor")
  )
  object Out:
    given ApiSchema[Out]  = deriveApiSchema
    given InOutCodec[Out] = deriveInOutCodec
    lazy val example      = Out()
  end Out

  lazy val example = customTask(
    In.example,
    Out.example
  )
end CheckIdentity
