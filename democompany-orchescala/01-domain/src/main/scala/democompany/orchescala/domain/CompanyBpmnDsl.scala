package democompany.orchescala.domain

/**
 * Add here company specific stuff, like documentation or custom elements.
 */
trait CompanyBpmnDsl extends BpmnDsl:
  // override def companyDescr = ??? //TODO Add your specific Company Description!
end CompanyBpmnDsl

trait CompanyBpmnProcessDsl extends BpmnProcessDsl, CompanyBpmnDsl
trait CompanyBpmnServiceTaskDsl extends BpmnServiceTaskDsl, CompanyBpmnDsl
trait CompanyBpmnCustomTaskDsl extends BpmnCustomTaskDsl, CompanyBpmnDsl
trait CompanyBpmnDecisionDsl extends BpmnDecisionDsl, CompanyBpmnDsl
trait CompanyBpmnUserTaskDsl extends BpmnUserTaskDsl, CompanyBpmnDsl
trait CompanyBpmnMessageEventDsl extends BpmnMessageEventDsl, CompanyBpmnDsl
trait CompanyBpmnSignalEventDsl extends BpmnSignalEventDsl, CompanyBpmnDsl
trait CompanyBpmnTimerEventDsl extends BpmnTimerEventDsl, CompanyBpmnDsl
