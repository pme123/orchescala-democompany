package democompany.orchescala.worker

import orchescala.worker.op.OpContext
import scala.reflect.ClassTag

class CompanyEngineOpContext(restApiClient: CompanyRestApiOpClient) extends OpContext:


  override def sendRequest[ServiceIn: Encoder, ServiceOut: {Decoder, ClassTag}](
      request: RunnableRequest[ServiceIn]
  ): SendRequestType[ServiceOut] =
    restApiClient.sendRequest(request)

end CompanyEngineOpContext

