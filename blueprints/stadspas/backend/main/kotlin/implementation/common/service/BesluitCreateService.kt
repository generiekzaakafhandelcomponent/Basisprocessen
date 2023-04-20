package com.ritense.valtimo.common.service

import com.fasterxml.jackson.core.JsonPointer
import com.ritense.besluit.connector.BesluitConnector
import com.ritense.besluit.domain.Besluit
import com.ritense.besluit.domain.BesluitType
import com.ritense.besluit.service.BesluitService
import com.ritense.connector.service.ConnectorService
import com.ritense.document.domain.Document
import com.ritense.document.service.DocumentService
import com.ritense.openzaak.service.ZaakInstanceLinkService
import com.ritense.resource.domain.OpenZaakResource
import com.ritense.resource.service.OpenZaakService
import mu.KotlinLogging
import java.util.UUID

class BesluitCreateService(
    private val zaakInstanceLinkService: ZaakInstanceLinkService,
    private val documentService: DocumentService,
    private val connectorService: ConnectorService,
    private val besluitService: BesluitService,
    private val openZaakService: OpenZaakService,
    private val besluitDocumentRequired: Boolean
) {

    fun createBesluitAndInformatieobjectRelatie(businessKey: String, besluitTypeOmschrijving: String) {
        val besluitType = besluitService.getBesluittypen().first {
            it.omschrijving == besluitTypeOmschrijving
        }

        val besluitConnector = connectorService.loadByClassName(BesluitConnector::class.java)

        val besluit = createBesluit(besluitConnector, besluitType, businessKey)

        createBesluitInformatieobjectRelatie(besluitConnector, besluit, businessKey)
    }

    private fun createBesluit(
        besluitConnector: BesluitConnector,
        besluitType: BesluitType,
        businessKey: String
    ): Besluit {
        val zaakInstanceLink = zaakInstanceLinkService.getByDocumentId(UUID.fromString(businessKey))

        logger.debug { "creating besluit for besluit type  '${besluitType.omschrijving}'" }

        return besluitConnector.createBesluit(
            zaakInstanceLink.zaakInstanceUrl,
            besluitType.url,
            businessKey
        )
    }

    private fun createBesluitInformatieobjectRelatie(
        besluitConnector: BesluitConnector,
        besluit: Besluit,
        businessKey: String
    ) {
        val besluitResource = getBesluitResource(documentService.get(businessKey))

        logger.debug { "creating besluit informatie object relatie for besluit '${besluit.identificatie}'" }

        if (besluitResource != null) {
            besluitConnector.createBesluitInformatieobjectRelatie(
                besluitResource.informatieObjectUrl,
                besluit.url
            )
        }
    }

    private fun getBesluitResource(document: Document): OpenZaakResource? {
        val besluitResourceId = getBesluitResourceUuid(document)

        return if (besluitResourceId != null) {
            openZaakService.getResource(besluitResourceId)
        } else {
            null
        }
    }

    private fun getBesluitResourceUuid(document: Document): UUID? {
        val besluitResourceUuidNode = document.content().asJson().at(JsonPointer.valueOf("/besluit"))

        return if (
            besluitResourceUuidNode.isMissingNode
            || besluitResourceUuidNode.isNull
            || besluitResourceUuidNode.textValue().equals("")
        ) {
            if (besluitDocumentRequired) {
                throw IllegalStateException("Dossier /besluit is empty. But valtimo.besluitDocumentRequired: true")
            } else {
                null
            }
        } else {
            if (!besluitResourceUuidNode.isTextual) {
                throw RuntimeException("Dossier /besluit doesn't contain UUID: `${besluitResourceUuidNode.toPrettyString()}`")
            } else {
                try {
                    UUID.fromString(besluitResourceUuidNode.textValue())
                } catch (e: IllegalArgumentException) {
                    throw RuntimeException("Dossier /besluit contains malformed UUID", e)
                }
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
