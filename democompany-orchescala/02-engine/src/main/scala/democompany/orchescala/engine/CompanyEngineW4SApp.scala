package democompany.orchescala.engine

import orchescala.engine.w4s.W4SEngineApp

trait CompanyEngineW4SApp extends W4SEngineApp:
  override def w4sEngineConfig: EngineConfig = companyEngineConfig

object CompanyEngineW4SApp extends CompanyEngineW4SApp

