// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
import Settings.*

ThisBuild / onLoadMessage                                  := loadingMessage
ThisBuild / versionScheme                                  := Some("semver-spec")
ThisBuild / libraryDependencySchemes += "io.github.pme123" %% "orchescala-api" % "early-semver"
ThisBuild / evictionErrorLevel                             := Level.Warn
ThisBuild / usePipelining                                  := true

lazy val root = project
  .in(file("."))
  .settings(
    sourcesInBase := false,
    projectSettings(),
    publicationSettings // Camunda artifacts
  ).aggregate(engine)

lazy val engine = project
  .in(file("./02-engine"))
  .settings(
    projectSettings(Some("engine")),
    publicationSettings,
    dockerSettings,
    libraryDependencies ++= engineDeps,
    testSettings,
    zioTestSettings
  ).enablePlugins(DockerPlugin, JavaAppPackaging)
