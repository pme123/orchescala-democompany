package democompany.services.domain

val etagHeaderName = "ETag"
val defaultEtag = "12345.234324234.2343423432"
lazy val etagHeaderMock: Map[String, String]          =
  Map(etagHeaderName -> defaultEtag)
