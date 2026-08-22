// DO NOT ADJUST. This file is replaced by `./helperCompany.scala init`.
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.11.7")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.2")
addSbtPlugin("org.typelevel"  % "laika-sbt"      % "1.3.2")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

addDependencyTreePlugin // sbt dependencyBrowseTreeHTML -> target/tree.html
