package implementation.stadspas.service

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ritense.document.service.DocumentService
import implementation.common.service.DateTimeService

class StadspasCasePropertiesInitService(
    private val documentService: DocumentService,
    private val dateTimeService: DateTimeService
) {
    fun initializeCaseProperties(businessKey: String) {
        val document = documentService.get(businessKey)

        val rootNode = jacksonObjectMapper().createObjectNode()

        documentService.modifyDocument(
            document,
            rootNode.apply {
                informatieverzoekenEnInformatieReactiesArraysInit(this)
                datumAanvraagOntvangenInit(this)
                escalatieObjectInit(this)
            }
        )
    }

    private fun informatieverzoekenEnInformatieReactiesArraysInit(rootNode: ObjectNode) {
        val beoordelingEnAfhandeling = jacksonObjectMapper().createObjectNode()

        beoordelingEnAfhandeling.putArray("informatieverzoeken")
        beoordelingEnAfhandeling.putArray("informatieReacties")

        rootNode.set<ObjectNode>("beoordelingEnAfhandeling", beoordelingEnAfhandeling)

    }

    private fun datumAanvraagOntvangenInit(rootNode: ObjectNode) {
        rootNode.put(
            "datumAanvraagOntvangen",
            dateTimeService.getEuropePatternDate(dateTimeService.getCurrentTimeStamp())
        )
    }

    private fun escalatieObjectInit(rootNode: ObjectNode) {
        val escalatie = jacksonObjectMapper().createObjectNode()

        escalatie.put("isGeescaleerd", false)
        rootNode.set<ObjectNode>("escalatie", escalatie)
    }
}
