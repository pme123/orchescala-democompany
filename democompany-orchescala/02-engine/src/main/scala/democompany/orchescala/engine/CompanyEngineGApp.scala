package democompany.orchescala.engine

import orchescala.engine.c7.SharedC7ClientManager
import orchescala.engine.c8.SharedC8ClientManager
import orchescala.engine.op.SharedOpClientManager
import orchescala.engine.domain.EngineType
import orchescala.engine.gateway.GProcessEngine
import zio.{ZIO, ZLayer}

trait CompanyEngineGApp extends EngineApp:

  /** The engines of the environment - see [[companyEngineTypes]]. */
  lazy val engineTypes: Seq[EngineType] = companyEngineTypes

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
