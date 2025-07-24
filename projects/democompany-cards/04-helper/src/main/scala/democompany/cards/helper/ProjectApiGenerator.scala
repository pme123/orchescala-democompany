package democompany.cards.helper

import orchescala.helper.openApi.*

object ProjectApiGenerator extends App:

  OpenApiGenerator().generate

  private given OpenApiConfig = OpenApiConfig(
    projectName = "democompany-cards",

  )
  private given ApiDefinition = OpenApiCreator().create

end ProjectApiGenerator