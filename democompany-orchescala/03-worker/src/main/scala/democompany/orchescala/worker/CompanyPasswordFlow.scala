package democompany.orchescala.worker

import orchescala.worker.c7.OAuth2WorkerClient

trait CompanyPasswordFlow extends OAuth2WorkerClient:

  def fssoRealm: String = ???
  def fssoBaseUrl: String = ???

 // override the config if needed or change the WorkerClient

end CompanyPasswordFlow
