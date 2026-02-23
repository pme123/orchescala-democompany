package democompany.orchescala.worker

import democompany.orchescala.engine.companyEngineConfig
import orchescala.engine.EngineConfig
import orchescala.worker.c7.C7Context

import scala.reflect.ClassTag

class CompanyEngineC7Context(restApiClient: CompanyRestApiC7Client) extends C7Context:


  override def sendRequest[ServiceIn: Encoder, ServiceOut: {Decoder, ClassTag}](
      request: RunnableRequest[ServiceIn]
  ): SendRequestType[ServiceOut] =
    restApiClient.sendRequest(request)

  def engineConfig: EngineConfig = companyEngineConfig

  def workerConfig: WorkerConfig = companyWorkerConfig
end CompanyEngineC7Context
