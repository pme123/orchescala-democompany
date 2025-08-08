package democompany.services.helper

import orchescala.helper.openApi.*

object ProjectApiGenerator extends App:

  OpenApiGenerator().generate

  private given OpenApiConfig = OpenApiConfig(
    projectName = "democompany-services",

  )
  private given ApiDefinition = OpenApiCreator().create

end ProjectApiGenerator