
import com.typesafe.config.ConfigFactory
import laika.ast.Path.Root
import laika.config.{LinkValidation, SyntaxHighlighting, Version, Versions}
import laika.format.Markdown.GitHubFlavor
import laika.helium.Helium
import laika.helium.config.{Favicon, HeliumIcon, IconLink}
import laika.sbt.LaikaPlugin.autoImport.*
import sbt.*
import sbt.Keys.*
import sbtbuildinfo.BuildInfoPlugin.autoImport.{BuildInfoKey, buildInfoKeys, buildInfoPackage}

import scala.jdk.CollectionConverters.asScalaBufferConverter

object Settings {

  val scalaV = "3.7.1"
  val orchescalaV = "0.3.0-SNAPSHOT"
  val camundaV = "7.23.0"
  val mUnitVersion = "1.1.0"
  val zioVersion = "2.1.17"
  // project
  val projectOrg = ProjectDef.org
  val projectV = ProjectDef.version
  val projectName = ProjectDef.name

  def buildInfoSettings(additionalKeys: BuildInfoKey*) = Seq(
    buildInfoKeys := Seq[BuildInfoKey](
      BuildInfoKey("name", s"$projectOrg-orchescala"),
      version,
      scalaVersion,
      sbtVersion,
      BuildInfoKey("orchescalaV", orchescalaV),
    ) ++ additionalKeys,
    buildInfoPackage := s"$projectOrg.orchescala"
  )

  def generalSettings(module: Option[String] = None) = Seq(
    scalaVersion := scalaV,
    autoImportSetting(module),
    scalacOptions ++= Seq(
      "-Xmax-inlines:200" // is declared as erased, but is in fact used
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

  // docs
  lazy val laikaSettings = Seq(
    sourcesInBase := false,
    laikaConfig := LaikaConfig.defaults
      .withConfigValue(LinkValidation.Local)
      .withConfigValue("orchescala.docs", "https://pme123.github.io/orchescala/")
      .withRawContent,
    Laika / sourceDirectories := Seq(baseDirectory.value / "src" / "docs")
    //  .failOnMessages(MessageFilter.None)
    //  .renderMessages(MessageFilter.None)
    ,
    laikaExtensions := Seq(GitHubFlavor, SyntaxHighlighting),
    laikaTheme := Helium.defaults.site
      .topNavigationBar(
        homeLink = IconLink.internal(Root / "index.md", HeliumIcon.home)
      )
      .site
      .favIcons(
        Favicon.internal(Root / "favicon.ico", sizes = "32x32")
      )
      .site
      .versions(versions)
      .build
  )

  lazy val config = ConfigFactory.parseFile(new File("00-docs/CONFIG.conf"))
  lazy val currentVersion = config.getString("release.tag")
  lazy val released = config.getBoolean("released")
  lazy val olderVersions = config.getList("releases.older").asScala
  lazy val versions = Versions
    .forCurrentVersion(Version(currentVersion, currentVersion).withLabel(if (released)
      "Stable"
    else "Dev"))
    .withOlderVersions(
      olderVersions.map(_.unwrapped().toString).map(v => Version(v, v)) *
    )

  def loadingMessage = s"""Successfully started.
                          |- Project: $projectOrg : $projectName : $projectV
                          |- Orchescala: $orchescalaV
                          |- Scala: $scalaV
                          |- Camunda: $camundaV
                          |""".stripMargin

  // dependencies
  val typesafeConfigDep = "com.typesafe" % "config" % "1.4.3"

  lazy val domainDeps = Seq(
    "io.github.pme123" %% "orchescala-domain" % orchescalaV
  )
  lazy val engineDeps = Seq(
    "io.github.pme123" %% "orchescala-engine-gateway" % orchescalaV
  )
  lazy val apiDeps = Seq(
    "io.github.pme123" %% "orchescala-api" % orchescalaV,
    typesafeConfigDep
  )
  lazy val dmnDeps = Seq(
    "io.github.pme123" %% "orchescala-dmn" % orchescalaV
  )
  lazy val simulationDeps = Seq(
    "io.github.pme123" %% "orchescala-simulation" % orchescalaV,
  )
  lazy val workerDeps = Seq(
    "io.github.pme123" %% "orchescala-worker-c7" % orchescalaV,
    "io.github.pme123" %% "orchescala-worker-c8" % orchescalaV,
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
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    homepage := Some(url(githubUrl)),
    startYear := Some(2021),
    // logLevel := Level.Debug,
    scmInfo := Some(
      ScmInfo(
        url(githubUrl),
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
      url = url("https://github.com/pme123")
    )
  )

  lazy val preventPublication = Seq(
    publish := {},
    publishArtifact := false,
    publishLocal := {}
  )
}
