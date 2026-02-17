package democompany.orchescala.worker

import democompany.orchescala.engine.companyEngineConfig
import orchescala.engine.EngineConfig
import orchescala.worker.c8.C8Context

import scala.reflect.ClassTag

class CompanyEngineC8Context(restApiClient: CompanyRestApiC8Client) extends C8Context:


  override def sendRequest[ServiceIn: Encoder, ServiceOut: {Decoder, ClassTag}](
      request: RunnableRequest[ServiceIn]
  ): SendRequestType[ServiceOut] =
    restApiClient.sendRequest(request)

  def engineConfig: EngineConfig = companyEngineConfig

  def workerConfig: WorkerConfig = companyWorkerConfig
end CompanyEngineC8Context
