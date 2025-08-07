package democompany.orchescala.worker

import democompany.orchescala.engine.CompanyEngineC8Config
import orchescala.engine.c8.C8SaasClient

trait CompanyC8Client  extends C8SaasClient, CompanyEngineC8Config
object CompanyC8Client extends CompanyC8Client
