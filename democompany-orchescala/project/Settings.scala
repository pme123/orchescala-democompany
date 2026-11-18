
import sbt.*
import sbt.Keys.*
import sbtbuildinfo.BuildInfoPlugin.autoImport.{BuildInfoKey, buildInfoKeys, buildInfoPackage}

object Settings {

  val scalaV       = "3.10.1"
  val orchescalaV  = "0.11.0-SNAPSHOT"
  val camundaV     = "7.24.0" // only as info
  val mUnitVersion = "1.2.4"
  val zioVersion = "2.1.24"
  val zioLoggingVersion = "2.5.3"
  val logbackVersion = "1.5.32"
  val jaxbApiVersion = "4.0.2"
  val angusMailV = "2.0.3"
  // project
  val projectOrg = ProjectDef.org
  val projectV = ProjectDef.version
  val projectName = ProjectDef.name

  def buildInfoSettings(additionalKeys: BuildInfoKey*) = Seq(
    buildInfoKeys := Seq[BuildInfoKey](
      BuildInfoKey("name", s"$projectOrg-orchescala"),
      // BuildInfoKey(...) around the keys: Scala 3 (sbt 2) converts them only in .sbt files
      BuildInfoKey(version),
      BuildInfoKey(scalaVersion),
      BuildInfoKey(sbtVersion),
      BuildInfoKey("orchescalaV", orchescalaV),
    ) ++ additionalKeys,
    buildInfoPackage := s"$projectOrg.orchescala"
  )

  def generalSettings(module: Option[String] = None) = Seq(
    scalaVersion := scalaV,
    autoImportSetting(module),
    scalacOptions ++= Seq(
      "-Xmax-inlines:200", // is declared as erased, but is in fact used
      "-Xmax-fuel:2000" // Scala 3.10: the derivation of large classes needs more than the default 550
      // "-Vprofile",
    )

  ) ++ module.map(m => name := s"$projectName-$m").toSeq

  def autoImportSetting(module: Option[String] = None) =
    scalacOptions +=
      (module.toSeq.map(m => s"orchescala.$m") ++
        Seq(
          "java.lang", "java.time", "scala", "scala.Predef", "orchescala.domain",
          "io.circe",
          "io.circe.generic.semiauto", "io.circe.derivation", "io.circe.syntax", "sttp.tapir",
          "sttp.tapir.json.circe"
        )).mkString(start = "-Yimports:", sep = ",", end = "")

  def loadingMessage = s"""Successfully started.
                          |- Project: $projectOrg : $projectName : $projectV
                          |- Orchescala: $orchescalaV
                          |- Scala: $scalaV
                          |- Camunda: $camundaV (C7, C8, Op)
                          |""".stripMargin

  // dependencies
  val typesafeConfigDep = "com.typesafe" % "config" % "1.4.3"

  lazy val domainDeps = Seq(
    "io.github.pme123" %% "orchescala-domain" % orchescalaV
  )
  lazy val engineDeps = Seq(
    // brings orchescala-engine and the engine implementations (c7, c8, op) with it
    "io.github.pme123" %% "orchescala-engine-gateway" % orchescalaV
  )
  lazy val apiDeps = Seq(
    "io.github.pme123" %% "orchescala-api" % orchescalaV,
    typesafeConfigDep
  )
  lazy val dmnDeps = Seq(
    // The DMN Tester - brings orchescala-dmn (the DSL) and
    // orchescala-dmntester (the model) with it.
    // The DMN engine is a Scala 2.13 jar whose FEEL parser drags in
    // geny_2.13, while os-lib brings geny_3 - the same library in two
    // cross versions, which sbt refuses. The engine works fine with
    // geny_3, so the 2.13 one is excluded here as well as upstream.
    ("io.github.pme123" %% "orchescala-dmntester-server" % orchescalaV)
      .excludeAll(ExclusionRule("com.lihaoyi", "geny_2.13"))
  )
  lazy val simulationDeps = Seq(
    "io.github.pme123" %% "orchescala-simulation" % orchescalaV,
  )
  lazy val workerDeps = Seq(
    "io.github.pme123" %% "orchescala-worker-c7" % orchescalaV,
    "io.github.pme123" %% "orchescala-worker-c8" % orchescalaV,
    "io.github.pme123" %% "orchescala-worker-op" % orchescalaV,
    // own data of the projects (CompanyPersistenceWorkerDsl) and mails (CompanyMailWorkerDsl)
    "io.github.pme123" %% "orchescala-persistence-postgres" % orchescalaV,
    "org.eclipse.angus" % "angus-mail" % angusMailV,
  )

  lazy val gatewayDeps = Seq(
    "ch.qos.logback"   % "logback-classic"      % logbackVersion % Runtime,
    "dev.zio"         %% "zio-logging-slf4j2"   % zioLoggingVersion,
    "jakarta.xml.bind" % "jakarta.xml.bind-api" % jaxbApiVersion,
    "io.github.pme123" %% "orchescala-gateway" % orchescalaV
  )

  lazy val helperDeps = apiDeps ++ Seq(
    "io.github.pme123" %% "orchescala-helper" % orchescalaV
  )

  lazy val unitTestSettings = Seq(
    libraryDependencies += "org.scalameta" %% "munit" % mUnitVersion % Test,
    testFrameworks += new TestFramework("munit.Framework")
  )

  lazy val zioTestSettings = Seq(
    libraryDependencies ++= zioTestDependencies,
    Test / parallelExecution := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  lazy val zioTestDependencies =
    Seq(
      "dev.zio" %% "zio-test" % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
    )


  // publish

  lazy val githubUrl = s"https://github.com/pme123/orchescala-$projectOrg"
  lazy val publicationSettings = Seq(
    // publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    licenses += License.MIT,
    homepage := Some(uri(githubUrl)),
    startYear := Some(2021),
    // logLevel := Level.Debug,
    scmInfo := Some(
      ScmInfo(
        uri(githubUrl),
        "scm:git:github.com:/pme123/orchescala"
      )
    ),
    developers := developerList
  )
  lazy val developerList = List(
    Developer(
      id = "pme123",
      name = "Pascal Mengelt",
      email = "pascal.mengelt@gmail.com",
      url = uri("https://github.com/pme123")
    )
  )

  lazy val preventPublication = Seq(
    publish := {},
    publishArtifact := false,
    publishLocal := {}
  )
}
