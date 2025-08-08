// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
import sbt.*

object ProjectDef {
  val org = "democompany"
  val name = "democompany-cards"
  val version = "0.1.0-SNAPSHOT"

  lazy val democompanyServicesVersion = "0.1.0-SNAPSHOT"

  lazy val domainDependencies = Seq(
    "democompany" % "democompany-services-domain" % democompanyServicesVersion
  )
  lazy val workerDependencies = Seq(
    "democompany" % "democompany-services-worker" % democompanyServicesVersion
  )

}