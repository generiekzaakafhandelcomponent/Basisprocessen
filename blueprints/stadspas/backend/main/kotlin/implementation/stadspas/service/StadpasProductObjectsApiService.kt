package implementation.stadspas.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ritense.connector.service.ConnectorService
import com.ritense.document.service.DocumentService
import com.ritense.objectsapi.domain.Object
import com.ritense.objectsapi.service.ObjectsApiConnector
import mu.KotlinLogging
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class StadspasProductObjectsApiService(
    private val connectorService: ConnectorService,
    private val documentService: DocumentService
) {

    fun createObject(documentId: String): Object {
        val stadspasProductObjectsApiConnector = connectorService
            .loadByName(STADSPAS_PRODUCT_OBJECTS_API_CONNECTOR) as ObjectsApiConnector

        logger.debug { "Creating new object in objectsApi from document with document ID '${documentId}'" }

        stadspasProductObjectsApiConnector.payload(getRootNode(documentId))

        return stadspasProductObjectsApiConnector.executeCreateObjectRequest()
    }

    private fun getRootNode(documentId: String): ObjectNode {
        val document = documentService.get(documentId)
        val documentContent = document.content().asJson() as ObjectNode

        val rootNode = jacksonObjectMapper().createObjectNode()

        val productMetadata = mapOf(
            "id" to documentId,
            "producttype" to "Stadspas",
            "doel" to "Stadspas",
            "bevoegdGezag" to "Gemeente ’s-Gravenhage"
        )

        productMetadata.forEach { data -> rootNode.put(data.key, data.value) }

        rootNode.set<ObjectNode>("data", getDataObjectNode(documentContent))

        return rootNode
    }

    private fun getDataObjectNode(documentContent: ObjectNode): ObjectNode {
        val zaakIdentificatie = documentContent.get("openzaak").get("identificatie").asText()

        val dataNode = jacksonObjectMapper().createObjectNode()

        dataNode.put("afgegevenIHKVZaak", zaakIdentificatie)
        dataNode.set<ObjectNode>("paseigenaar", getPaseigenaarNode(documentContent))
        dataNode.set<ObjectNode>("pasgeldigheid", getPasgeldigheidNode(documentContent))

        return dataNode
    }

    private fun getPaseigenaarNode(documentContent: ObjectNode): ObjectNode {
        val aanvrager = documentContent.get("aanvrager")
        val paseigenaarNode = jacksonObjectMapper().createObjectNode()

        val aanvragerData = mapOf(
            "naam" to aanvrager.get("volledigeNaam").asText(),

            // temporarily not required
//            "geslacht" to aanvrager.get("geslacht").asText(),
//            "geboortedatum" to aanvrager.get("geboortedatum").asText(),
        )

        aanvragerData.forEach { data -> paseigenaarNode.put(data.key, data.value) }

        return paseigenaarNode
    }

    private fun getPasgeldigheidNode(documentContent: ObjectNode): ObjectNode {
        val pasgeldigheidNode = jacksonObjectMapper().createObjectNode()
        val datumAanvraagToekenningNode =
            documentContent.get("beoordelingEnAfhandeling").get("besluit").get("datumAanvraagToekenning")

        val pasgeldigheidData = mapOf(
            "begindatum" to datumAanvraagToekenningNode.asText(),
            "einddatum" to getPasgeldigheidEinddatum(documentContent)
        )

        pasgeldigheidData.forEach { data -> pasgeldigheidNode.put(data.key, data.value) }

        return pasgeldigheidNode
    }

    private fun getPasgeldigheidEinddatum(documentContent: ObjectNode): String {
        val stadspasJaartalNode =
            documentContent.get("beoordelingEnAfhandeling").get("besluit").get("stadspasJaartal")
        val stadspasJaartal = stadspasJaartalNode.asText().substring(6, 10).toInt()

        val einddatumStadspas = ZonedDateTime.of(stadspasJaartal, 12, 31, 0, 0, 0, 0, ZoneId.systemDefault())

        return einddatumStadspas.format(DateTimeFormatter.ISO_INSTANT)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val STADSPAS_PRODUCT_OBJECTS_API_CONNECTOR = "stadspas-product-stadspas"
    }
}
