// DO NOT ADJUST. This file is replaced by `./helper.scala update`.

import com.typesafe.sbt.SbtNativePackager.Docker
import com.typesafe.sbt.packager.Keys.*
import sbt.*
import sbt.Keys.*

object Settings {

  val scalaV = "3.8.3"
  val customer = ProjectDef.org
  val customerOrchescalaV = "0.1.0-SNAPSHOT"
  // to override the version defined in customerOrchescala
  val orchescalaV = "0.7.0-SNAPSHOT"

  // other dependencies
  // run worker
  val mUnitVersion = "1.2.4"
  val mUnit = "org.scalameta" %% "munit" % mUnitVersion % Test
  val zioVersion = "2.1.24"
  val logbackVersion = "1.5.32"
  val jaxbApiVersion = "4.0.2"
  
  def projectSettings(
                       module: Option[String] = None,
                       postfix: Option[String] = None
                     ) = Seq(
    name := s"${ProjectDef.name}${module.map(p => s"-$p").getOrElse("")}",
    organization := ProjectDef.org,
    version := ProjectDef.version,
    scalaVersion := scalaV,
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-Xmax-inlines:200", // is declared as erased, but is in fact used
      "-language:implicitConversions", // allow feature - as the DSLs use it - otherwise there are feature warnings
      // "-rewrite", "-source", "3.4-migration", // migrate automatically to scala 3.4
      // "-Vprofile",
    ),
    javaOptions ++= Seq(
      "-Xmx3g",
      "-Xss2m",
      "-XX:+UseG1GC",
      "-XX:InitialCodeCacheSize=512m",
      "-XX:ReservedCodeCacheSize=512m",
      "-Dfile.encoding=UTF8"
    ),
    credentials ++= Seq(),
    resolvers ++= Seq(releaseRepo),
    autoImportSetting(
      (postfix orElse module).toSeq.flatMap{ x =>
         val proj = x
         Seq(s"orchescala.$x", s"$customer.orchescala.$proj")
      }
    )
  )


  lazy val domainDeps = ProjectDef.domainDependencies ++
    Seq(
      customer %% s"$customer-orchescala-domain" % customerOrchescalaV,
      "io.github.pme123" %% "orchescala-domain" % orchescalaV
    )

  lazy val apiDeps = 
    Seq(
      customer %% s"$customer-orchescala-api" % customerOrchescalaV,
      "io.github.pme123" %% "orchescala-api" % orchescalaV
    )

  lazy val dmnDeps = 
    Seq(
      customer %% s"$customer-orchescala-dmn" % customerOrchescalaV,
      ("io.github.pme123" %% "orchescala-dmntester-server" % orchescalaV)
        .exclude("com.lihaoyi", "geny_2.13")
    )

  lazy val simulationDeps = 
    Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion % Test,
      customer %% s"$customer-orchescala-simulation" % customerOrchescalaV,
      "io.github.pme123" %% "orchescala-simulation" % orchescalaV
    )

  lazy val workerDeps = ProjectDef.workerDependencies ++
    Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion % Runtime,
      "jakarta.xml.bind" % "jakarta.xml.bind-api" % jaxbApiVersion,
      customer %% s"$customer-orchescala-worker" % customerOrchescalaV,
      "io.github.pme123" %% "orchescala-worker" % orchescalaV
    )

  lazy val preventPublication = Seq(
    publish / skip := true,
    publish := {},
    publishArtifact := false,
    publishLocal := {}
  )

  lazy val publicationSettings = Seq(
    publishTo := Some(releaseRepo),
    // Enables publishing to maven repo
    publishMavenStyle := true,
    packageDoc / publishArtifact := false,
    // logLevel := Level.Debug,
    // disable using the Scala version in output paths and artifacts
    crossPaths := false
  )
// Credentials

// Repos
  // 
  lazy val releaseRepoStr = 
    "???"
  lazy val releaseRepo: MavenRepository = "gitlab" at releaseRepoStr

  lazy val dockerSettings = Seq()
  lazy val testSettings = Seq(
    libraryDependencies += mUnit,
    Test / parallelExecution := true,
    testFrameworks += new TestFramework("munit.Framework")
  )
  lazy val simulationSettings = Seq(
    Test / parallelExecution := false,
    testFrameworks += new TestFramework("orchescala.simulation.SimulationTestFramework")
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
  


  lazy val loadingMessage = s"""Successfully started
- Dependencies:
  - Orchescala: $orchescalaV
  - Camunda: 7.24.0
  - Customer-Orchescala: $customerOrchescalaV
  - Scala: $scalaV

- Package Config:
  - org: ${ProjectDef.org}
  - name: ${ProjectDef.name}
  - version: ${ProjectDef.version}
  - dependencies: ${ProjectDef.domainDependencies.map(_.toString()).sorted.mkString("\n    - ", "\n    - ", "")}
  """
  def autoImportSetting(customAutoSettings: Seq[String]) =
    scalacOptions +=
      (customAutoSettings ++
        Seq(
          "java.lang",
          "java.time", 
          "scala",
          "scala.Predef",
          "orchescala.domain",
          s"$customer.orchescala.domain",
          "io.circe.syntax", 
          "sttp.tapir.json.circe",
          "io.scalaland.chimney.dsl",
          "io.github.iltotore.iron",
          "io.github.iltotore.iron.constraint",
          "io.github.iltotore.iron.circe",
          "sttp.tapir.codec.iron"
        )).mkString(start = "-Yimports:", sep = ",", end = "")

}