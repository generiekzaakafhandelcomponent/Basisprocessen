package implementation.stadspas.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ritense.document.domain.Document
import com.ritense.document.service.DocumentService
import implementation.common.service.DateTimeService

class StadspasProcessService(
    private val documentService: DocumentService,
    private val dateTimeService: DateTimeService,
    val besluitTypeOmschrijving: String
) {

    fun getHersteltermijnVanLaatsteInformatieverzoek(businessKey: String): Boolean {
        val document = getDocumentById(businessKey)

        val informatieverzoekenArrayNode = getInformatieverzoekenArrayNode(document)

        return if (!informatieverzoekenArrayNode.isMissingNode) {
            informatieverzoekenArrayNode
                .get(informatieverzoekenArrayNode.size() - 1)
                .get("hersteltermijn")
                .asBoolean()
        } else {
            false
        }
    }

    fun setStopHersteltermijnForLatestVerzoekInDocument(businessKey: String): String {
        val timestamp = dateTimeService.getCurrentTimeStamp()

        getDocumentById(businessKey).apply {
            documentService.modifyDocument(
                this,
                this.content().asJson()
                    .let {
                        val latestVerzoek =
                            it.at("/beoordelingEnAfhandeling/informatieverzoeken").last() as ObjectNode

                        latestVerzoek.put("stopHersteltermijn", timestamp)
                        it
                    }
            )
        }

        return timestamp
    }

    fun getPropertyOfLatestVerzoekFromDocument(businessKey: String, property: String): String {
        return getDocumentById(businessKey).content().asJson().let {
            val latestVerzoek =
                it.at("/beoordelingEnAfhandeling/informatieverzoeken").last()

            latestVerzoek.get(property)?.textValue()
                ?: throw NoSuchElementException("Property '${property}' was not found in the latest verzoek")
        }
    }

    fun setStartHersteltermijnInDocument(businessKey: String) {
        val document = getDocumentById(businessKey)

        val informatieverzoekenArrayNode = getInformatieverzoekenArrayNode(document)

        documentService.modifyDocument(document, getRootNodeInformatieverzoeken(informatieverzoekenArrayNode))
    }

    // variable for email template
    fun getDeadlineOfLatestVerzoek(businessKey: String): String {
        val document = getDocumentById(businessKey)

        val informatieverzoekenArrayNode = getInformatieverzoekenArrayNode(document)

        if (!informatieverzoekenArrayNode.isEmpty) {
            val deadlineLatestVerzoek = getPropertyOfLatestVerzoekFromDocument(businessKey, "deadline")
            return dateTimeService.getEuropePatternDate(deadlineLatestVerzoek)
        }
        return ""
    }

    fun determineBeschikkingTemplate(businessKey: String): String {
        val document = getDocumentById(businessKey)
        val eindconclusie =
            document.content().asJson().get("beoordelingEnAfhandeling").get("besluit").get("eindconclusie").asText()

        return when (eindconclusie) {
            "Aanvraag toekennen" -> STADSPAS_BESLUITBRIEF_TOEWIJZING
            "Aanvraag afwijzen" -> STADSPAS_BESLUITBRIEF_AFWIJZING
            "Aanvraag buiten behandeling stellen" -> STADSPAS_BESLUITBRIEF_AFWIJZING
            else -> throw IllegalArgumentException("No beschikking template available for the besluit '${eindconclusie}'")
        }
    }

    fun setDefaultCommunicatievoorkeur(value: String, documentId: String ){
        val document = getDocumentById(documentId)
        val rootNode = jacksonObjectMapper().createObjectNode()
        val communicatievoorkeuren = jacksonObjectMapper().createArrayNode()
        communicatievoorkeuren.add(value)
        rootNode.set<JsonNode>("communicatievoorkeuren", communicatievoorkeuren)

        documentService.modifyDocument(document, rootNode)
    }

    private fun getInformatieverzoekenArrayNode(document: Document) = document
        .content()
        .asJson()
        .get("beoordelingEnAfhandeling")
        .get("informatieverzoeken") as ArrayNode

    private fun getRootNodeInformatieverzoeken(informatieverzoekenArrayNode: ArrayNode): ObjectNode {

        val rootNode = jacksonObjectMapper().createObjectNode()
        val beoordelingEnAfhandeling = jacksonObjectMapper().createObjectNode()
        val laatsteIndexInformatieverzoekenArray = informatieverzoekenArrayNode.size() - 1

        if (!informatieverzoekenArrayNode.isMissingNode) {
            val laatsteInformatieverzoek = informatieverzoekenArrayNode
                .get(laatsteIndexInformatieverzoekenArray) as ObjectNode

            laatsteInformatieverzoek.put("startHersteltermijn", dateTimeService.getCurrentTimeStamp())
            informatieverzoekenArrayNode[laatsteIndexInformatieverzoekenArray] = laatsteInformatieverzoek
            beoordelingEnAfhandeling.set<ArrayNode>("informatieverzoeken", informatieverzoekenArrayNode)
            rootNode.set<ObjectNode>("beoordelingEnAfhandeling", beoordelingEnAfhandeling)
        }

        return rootNode
    }

    private fun getDocumentById(businessKey: String): Document {
        return documentService.get(businessKey)
    }

    companion object {
        private const val STADSPAS_BESLUITBRIEF_TOEWIJZING = "stadspas-besluitbrief-toewijzen"
        private const val STADSPAS_BESLUITBRIEF_AFWIJZING = "stadspas-besluitbrief-afwijzing"
    }
}
