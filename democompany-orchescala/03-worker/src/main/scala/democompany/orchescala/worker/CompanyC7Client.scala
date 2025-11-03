package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC7Config
import orchescala.worker.c7.OAuth2WorkerClient
import scala.concurrent.duration.*

trait CompanyC7Client extends OAuth2WorkerClient, CompanyEngineC7Config


object CompanyC7Client extends CompanyC7Client
