// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
import Settings.*

ThisBuild / onLoadMessage := loadingMessage
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / libraryDependencySchemes += "io.github.pme123" %% "orchescala-api" % "early-semver"
ThisBuild / evictionErrorLevel := Level.Warn
ThisBuild / usePipelining := true


lazy val root = project
  .in(file("."))
  .settings(
    sourcesInBase := false,
    projectSettings(),
    publicationSettings, //Camunda artifacts
  ).aggregate(domain, engine, api, dmn, simulation, worker)


lazy val domain = project
  .in(file("./01-domain"))
  .settings(
    projectSettings(Some("domain")),
    publicationSettings,
    libraryDependencies ++= domainDeps,
    testSettings
  )
  .aggregate()
  .dependsOn()


  


lazy val engine = project
  .in(file("./02-engine"))
  .settings(
    projectSettings(Some("engine")),
    publicationSettings,
    libraryDependencies ++= engineDeps,
    testSettings
  ).dependsOn(domain)
  
  


lazy val api = project
  .in(file("./03-api"))
  .settings(
    projectSettings(Some("api")),
    publicationSettings,
    libraryDependencies ++= apiDeps
  ).dependsOn(engine)
  
  


lazy val dmn = project
  .in(file("./03-dmn"))
  .settings(
    projectSettings(Some("dmn")),
    preventPublication,
    libraryDependencies ++= dmnDeps
  ).dependsOn(engine)
  
  


lazy val simulation = project
  .in(file("./03-simulation"))
  .settings(
    projectSettings(Some("simulation")),
    preventPublication,
    libraryDependencies ++= simulationDeps,
    simulationSettings
  ).dependsOn(engine)
  
  


lazy val worker = project
  .in(file("./03-worker"))
  .settings(
    projectSettings(Some("worker")),
    publicationSettings,
    libraryDependencies ++= workerDeps,
    dockerSettings,
    testSettings,
    zioTestSettings
  ).dependsOn(engine)
  
  .enablePlugins(DockerPlugin, JavaAppPackaging)


