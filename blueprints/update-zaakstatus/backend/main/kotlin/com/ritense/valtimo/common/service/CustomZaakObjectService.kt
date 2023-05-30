package com.ritense.valtimo.common.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ritense.openzaak.service.impl.OpenZaakConfigService
import com.ritense.openzaak.service.impl.OpenZaakRequestBuilder
import com.ritense.openzaak.service.impl.OpenZaakTokenGeneratorService
import com.ritense.openzaak.service.impl.model.zaak.Zaak
import com.ritense.openzaak.service.ZaakService
import com.ritense.valtimo.common.domain.ZaakObjectCreateRequestBody
import com.ritense.valtimo.common.domain.ZaakObjectResponse
import mu.KotlinLogging
import org.springframework.boot.web.client.RestTemplateBuilder
import java.util.UUID

class CustomZaakObjectService(
    private val openZaakConfigService: OpenZaakConfigService,
    private val openZaakTokenGeneratorService: OpenZaakTokenGeneratorService,
    private val zaakService: ZaakService
) {

    fun linkObjectToZaak(businessKey: String, objectUrl: String, objecttypeOverige: String) {
        linkZaakObjectToZaak(
            zaakService.getZaakByDocumentId(UUID.fromString(businessKey)),
            objectUrl,
            objecttypeOverige
        )
    }

    private fun linkZaakObjectToZaak(
        zaak: Zaak?,
        objectUrl: String,
        objectTypeOverige: String
    ): ZaakObjectResponse {
        val zaakObjectCreateRequestBody = ZaakObjectCreateRequestBody(
            zaak!!.url.toString(),
            objectUrl,
            "overige",
            objectTypeOverige,
        )

        logger.info(
            "Linking object $objectUrl" +
                "to ${zaak.identificatie}"
        )

        return OpenZaakRequestBuilder(
            RestTemplateBuilder().build(),
            openZaakConfigService,
            openZaakTokenGeneratorService
        )
            .path("zaken/api/v1/zaakobjecten")
            .post()
            .body(jacksonObjectMapper().valueToTree(zaakObjectCreateRequestBody))
            .build()
            .execute(ZaakObjectResponse::class.java)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
