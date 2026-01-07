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
  ).aggregate(domain, api, dmn, simulation, worker, helper)


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


  


lazy val api = project
  .in(file("./03-api"))
  .settings(
    projectSettings(Some("api")),
    publicationSettings,
    libraryDependencies ++= apiDeps
  ).dependsOn(domain)
  
  


lazy val dmn = project
  .in(file("./03-dmn"))
  .settings(
    projectSettings(Some("dmn")),
    preventPublication,
    libraryDependencies ++= dmnDeps
  ).dependsOn(domain)
  
  


lazy val simulation = project
  .in(file("./03-simulation"))
  .settings(
    projectSettings(Some("simulation")),
    preventPublication,
    libraryDependencies ++= simulationDeps,
    simulationSettings
  ).dependsOn(domain)
  
  


lazy val worker = project
  .in(file("./03-worker"))
  .settings(
    projectSettings(Some("worker")),
    publicationSettings,
    libraryDependencies ++= workerDeps,
    dockerSettings,
    testSettings,
    zioTestSettings
  ).dependsOn(domain)
  
  .enablePlugins(DockerPlugin, JavaAppPackaging)


lazy val helper = project
  .in(file("./04-helper"))
  .settings(
    projectSettings(Some("helper")),
    publicationSettings,
    libraryDependencies ++= helperDeps
  ).dependsOn(api, dmn, simulation, worker)
  
  


