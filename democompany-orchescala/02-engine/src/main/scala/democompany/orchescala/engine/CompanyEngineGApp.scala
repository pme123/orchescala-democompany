package democompany.orchescala.engine

import orchescala.engine.c7.SharedC7ClientManager
import orchescala.engine.c8.SharedC8ClientManager
import orchescala.engine.op.SharedOpClientManager
import orchescala.engine.domain.EngineType
import orchescala.engine.gateway.GProcessEngine
import zio.{ZIO, ZLayer}

trait CompanyEngineGApp extends EngineApp:

  /** The engines of the environment - `COMPANY_ENGINES`, e.g. `Op` or `C7,C8,Op`; the first is the
    * default engine. Without it all three (C7, C8, Op). An engine that is not chosen is not
    * created - an environment without Camunda 8 needs no C8 settings.
    */
  lazy val engineTypes: Seq[EngineType] =
    sys.env
      .get("COMPANY_ENGINES")
      .map(_.split(",").toSeq.map(_.trim).filter(_.nonEmpty).map: name =>
        EngineType.values
          .find(_.toString.equalsIgnoreCase(name))
          .getOrElse(throw IllegalArgumentException(s"COMPANY_ENGINES: unknown engine '$name' - use C7, C8 or Op")))
      .getOrElse(Seq(EngineType.C7, EngineType.C8, EngineType.Op))

  // Override this to provide the ZIO layers required by this simulation
  lazy val requiredLayers: Seq[ZLayer[Any, Nothing, Any]] =
    engineTypes.flatMap:
      case EngineType.C7 => CompanyEngineC7App.requiredLayers
      case EngineType.C8 => CompanyEngineC8App.requiredLayers
      case EngineType.Op => CompanyEngineOpApp.requiredLayers
      case _             => Seq.empty

  // Override engineZIO to create the engine within the SharedC8ClientManager environment
  override def engineZIO: ZIO[Any, Nothing, ProcessEngine] =
    (for
      engines                 <- ZIO.foreach(engineTypes.collect(engineOf))(identity)
      given Seq[ProcessEngine] = engines // the order of COMPANY_ENGINES - the first is the default
    yield GProcessEngine()(using companyEngineConfig))
      .provideLayer(
        /*SharedC7ClientManager.layer ++ SharedC8ClientManager.layer ++ */ SharedOpClientManager.layer
      )

  private lazy val engineOf: PartialFunction[EngineType, ZIO[Any, Nothing, ProcessEngine]] =
    case EngineType.C7 => CompanyEngineC7App.engineZIO
    case EngineType.C8 => CompanyEngineC8App.engineZIO
    case EngineType.Op => CompanyEngineOpApp.engineZIO

end CompanyEngineGApp
