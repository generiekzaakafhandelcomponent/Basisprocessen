package implementation.stadspas.service

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ritense.document.service.DocumentService
import implementation.common.service.DateTimeService

class StadspasOpgegevenDataToBeoordelingService(
    private val documentService: DocumentService
) {
    fun copyOpgegevenDataToBeoordeling(businessKey: String) {
        val documentContent = getDocumentById(businessKey).content().asJson()

        val root = jacksonObjectMapper().createObjectNode()
        val beoordeling = jacksonObjectMapper().createObjectNode()
        beoordeling.set<ObjectNode>("aanvraaggegevens", documentContent.get("aanvraaggegevens"))
        beoordeling.set<ObjectNode>("inkomenKlant", documentContent.get("inkomenKlant"))
        beoordeling.set<ObjectNode>("inkomenPartner", documentContent.get("inkomenPartner"))
        beoordeling.set<ObjectNode>("vermogen", documentContent.get("vermogen"))

        root.set<ObjectNode>("beoordelingEnAfhandeling", beoordeling)

        documentService.modifyDocument(getDocumentById(businessKey), root)

    }

    fun getDocumentById(businessKey: String): Document {
        return documentService.get(businessKey)
    }
}
