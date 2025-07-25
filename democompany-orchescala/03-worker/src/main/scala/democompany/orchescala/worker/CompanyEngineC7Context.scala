package democompany.orchescala.worker

import orchescala.worker.c7.C7Context
import scala.reflect.ClassTag

class CompanyEngineC7Context(restApiClient: CompanyRestApiClient) extends C7Context:


  override def sendRequest[ServiceIn: Encoder, ServiceOut: {Decoder, ClassTag}](
      request: RunnableRequest[ServiceIn]
  ): SendRequestType[ServiceOut] =
    restApiClient.sendRequest(request)

end CompanyEngineC7Context
