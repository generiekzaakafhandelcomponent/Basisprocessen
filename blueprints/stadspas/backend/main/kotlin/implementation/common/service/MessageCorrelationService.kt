package implementation.common.service

import com.ritense.document.domain.Document
import com.ritense.document.domain.impl.JsonSchemaDocumentId
import com.ritense.document.service.DocumentService
import com.ritense.processdocument.service.ProcessDocumentAssociationService
import java.util.UUID
import org.camunda.bpm.engine.RuntimeService

class MessageCorrelationService(
    private val runtimeService: RuntimeService,
    private val documentService: DocumentService,
    private val associationService: ProcessDocumentAssociationService
) {
    fun sendCorrelationMessage(message: String) {
        runtimeService.correlateMessage(message)
    }

    fun sendCorrelationMessageByBusinessKey(message: String, businessKey: String) {
        runtimeService.createMessageCorrelation(message)
            .processInstanceBusinessKey(businessKey)
            .correlate()
    }

    fun sendCorrelationMessageByBusinessKeyWithVariable(
        message: String,
        businessKey: String,
        variableName: String,
        variableValue: Any
    ) {
        runtimeService.createMessageCorrelation(message)
            .processInstanceBusinessKey(businessKey)
            .setVariable(variableName, variableValue)
            .correlate()
    }

    fun sendCorrelationMessageByBusinessKeyWithSourceBusinessKey(
        messageName: String,
        targetBusinessKey: String,
        sourceBusinessKey: String
    ) {
        runtimeService.createMessageCorrelation(messageName)
            .processInstanceBusinessKey(targetBusinessKey)
            .setVariable("messageOriginBusinessKey", sourceBusinessKey)
            .correlate()
    }

    fun sendCorrelationStartMessageByBusinessKey(message: String, process: String, businessKey: String) {
        documentService.findBy(JsonSchemaDocumentId.existingId(UUID.fromString(businessKey)))
            .ifPresentOrElse({ document: Document? ->
                val processInstance = runtimeService.createMessageCorrelation(message)
                    .processInstanceBusinessKey(businessKey)
                    .correlateStartMessage()
                if (document != null) {
                    associationService.createProcessDocumentInstance(
                        processInstance.id,
                        UUID.fromString(document.id().toString()),
                        process
                    )
                }
            }, { throw RuntimeException("Document not found!") }
            )
    }

    fun sendCorrelationStartMessageByBusinessKeyWithVariables(
        businessKey: String,
        message: String,
        processName: String,
        variables: Map<String, Any>
    ) {
        val processInstance = runtimeService.startProcessInstanceByMessage(message, businessKey, variables)

        associationService.createProcessDocumentInstance(
            processInstance.id,
            UUID.fromString(businessKey),
            processName
        )
    }
}
