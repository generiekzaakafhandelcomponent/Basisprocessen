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

    fun getPropertyOfLatestVerzoekFromDocument(businessKey: String, property: String): Any {
        return getDocumentById(businessKey).content().asJson().let {
            val latestVerzoek =
                it.at("/beoordelingEnAfhandeling/informatieverzoeken").last()

            latestVerzoek.get(property)?.toValue()
                ?: throw NoSuchElementException("Property '${property}' was not found in the latest verzoek")
        }
    }

    //Can be used in camunda (JUEL) expressions.
    fun getPropertyOfLatestVerzoekOrDefault(businessKey: String, property: String, default: Any): Any {
        return getDocumentById(businessKey).content().asJson().let {
            val latestVerzoek =
                it.at("/beoordelingEnAfhandeling/informatieverzoeken").last()

            latestVerzoek.get(property)?.toValue() ?: default
        }
    }

    val toValue: JsonNode.() -> Any? = {
        when {
            this.isValueNode || this.isContainerNode -> jacksonObjectMapper().treeToValue(this)
            else -> null
        }
    }

    fun setStartHersteltermijnInDocument(businessKey: String) {
        val document = getDocumentById(businessKey)

        val informatieverzoekenArrayNode = getInformatieverzoekenArrayNode(document)

        documentService.modifyDocument(document, getRootNodeInformatieverzoeken(informatieverzoekenArrayNode))
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
}
