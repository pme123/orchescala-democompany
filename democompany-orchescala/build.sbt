// // This file was created with `./helperCompany.scala init` - to reset delete it and run the command.
import sbt.*
import sbt.Keys.*
import Settings.*

ThisBuild / version := projectV
ThisBuild / organization := projectOrg
ThisBuild / onLoadMessage := loadingMessage

lazy val root = (project in file("."))
  .settings(name := projectName, sourcesInBase := false)
  .settings(preventPublication)
  .aggregate(
    domain,
    engine,
    api,
    dmn,
    simulation,
    worker,
    helper,
    docs
  )

lazy val domain = project
  .in(file("./01-domain"))
  .settings(generalSettings(Some("domain")))
  .settings(publicationSettings)
  .settings(libraryDependencies ++= domainDeps)
  .settings(buildInfoSettings())
  .enablePlugins(BuildInfoPlugin)

lazy val engine = project
  .in(file("./02-engine"))
  .settings(generalSettings(Some("engine")))
  .settings(publicationSettings)
  .settings(libraryDependencies ++= engineDeps)
  .dependsOn(domain)

lazy val api = project
  .in(file("./03-api"))
  .settings(generalSettings(Some("api")))
  .settings(publicationSettings)
  .settings(unitTestSettings)
  .settings(libraryDependencies ++= apiDeps)
  .dependsOn(domain, engine)

lazy val dmn = project
  .in(file("./03-dmn"))
  .settings(generalSettings(Some("dmn")))
  .settings(publicationSettings)
  .settings(libraryDependencies ++= dmnDeps)
  .dependsOn(domain)

lazy val simulation = project
  .in(file("./03-simulation"))
  .settings(generalSettings(Some("simulation")))
  .settings(publicationSettings)
  .settings(libraryDependencies ++= simulationDeps)
  .dependsOn(engine)

lazy val worker = project
  .in(file("./03-worker"))
  .settings(generalSettings(Some("worker")))
  .settings(publicationSettings)
  .settings(unitTestSettings)
  .settings(libraryDependencies ++= workerDeps)
  .dependsOn(engine)

lazy val helper = project
  .in(file("./04-helper"))
  .settings(generalSettings(Some("helper")))
  .settings(publicationSettings)
  .settings(libraryDependencies ++= helperDeps)
  .dependsOn(api, simulation)

lazy val docs = project
  .in(file("./00-docs"))
  .settings(
    name := s"$projectName-docs"
  )
  .settings(generalSettings())
  .settings(preventPublication)
  .dependsOn(helper)
  .settings(laikaSettings)
  .enablePlugins(LaikaPlugin)


