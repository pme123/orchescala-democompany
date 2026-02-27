package democompany.orchescala.worker

import democompany.orchescala.engine.companyEngineConfig
import orchescala.engine.EngineConfig
import orchescala.worker.op.OperatonContext
import scala.reflect.ClassTag

class CompanyEngineOpContext(restApiClient: CompanyRestApiOpClient) extends OperatonContext:


  override def sendRequest[ServiceIn: Encoder, ServiceOut: {Decoder, ClassTag}](
      request: RunnableRequest[ServiceIn]
  ): SendRequestType[ServiceOut] =
    restApiClient.sendRequest(request)

  def engineConfig: EngineConfig = companyEngineConfig

  def workerConfig: WorkerConfig = companyWorkerConfig
end CompanyEngineOpContext

