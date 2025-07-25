package democompany.orchescala.worker

import orchescala.worker.c8.C8Context

import scala.reflect.ClassTag

class CompanyEngineC8Context(restApiClient: CompanyRestApiC7Client) extends C8Context:


  override def sendRequest[ServiceIn: Encoder, ServiceOut: {Decoder, ClassTag}](
      request: RunnableRequest[ServiceIn]
  ): SendRequestType[ServiceOut] =
    restApiClient.sendRequest(request)

end CompanyEngineC8Context
