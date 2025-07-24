// DO NOT ADJUST. This file is replaced by `./helperCompany.scala init`.
addDependencyTreePlugin // sbt dependencyBrowseTreeHTML -> target/tree.html

addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.2")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.12.0")

// docs
addSbtPlugin("org.typelevel" % "laika-sbt" % "1.3.0")

// docker (optional)
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.0")
