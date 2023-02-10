package implementation.common.service

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ritense.document.domain.Document
import com.ritense.document.domain.impl.JsonSchemaDocumentId
import com.ritense.document.service.DocumentService
import com.ritense.openzaak.service.ZaakService
import java.util.UUID

class ZaakPropertiesService(
    private val zaakService: ZaakService,
    private val documentService: DocumentService
) {

    fun storeSelectedZaakPropertiesInDocumentRoot(businessKey: String, vararg selectedProperties: String) {
        val document = getDocumentById(businessKey)
        val zaak = zaakService.getZaakByDocumentId(UUID.fromString(businessKey))
        val rootJsonNode = jacksonObjectMapper().createObjectNode()
        val zaakMap = jacksonObjectMapper().convertValue(zaak, HashMap::class.java)
        val filteredZaakNode =
            jacksonObjectMapper().valueToTree<ObjectNode>(
                zaakMap.filter { selectedProperties.contains(it.key) }
            )

        documentService.modifyDocument(document, rootJsonNode.set(ZAAK_NODE_NAME, filteredZaakNode))
    }

    private fun getDocumentById(documentId: String): Document {
        return documentService.findBy(JsonSchemaDocumentId.existingId(UUID.fromString(documentId))).orElseThrow()
    }

    companion object {
        const val ZAAK_NODE_NAME = "openzaak"
    }
}
