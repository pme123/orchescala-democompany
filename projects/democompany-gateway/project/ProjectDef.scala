// DO NOT ADJUST. This file is replaced by `./helper.scala update`.
import sbt.*

object ProjectDef {
  val org = "democompany"
  val name = "democompany-gateway"
  val version = "0.1.0-SNAPSHOT"

  lazy val democompanyCardsVersion = "0.1.0-SNAPSHOT"
  lazy val democompanyServicesVersion = "0.1.0-SNAPSHOT"

  lazy val gatewayDependencies = Seq(
    "democompany" % "democompany-cards-worker" % democompanyCardsVersion,
    "democompany" % "democompany-services-worker" % democompanyServicesVersion
  )

}