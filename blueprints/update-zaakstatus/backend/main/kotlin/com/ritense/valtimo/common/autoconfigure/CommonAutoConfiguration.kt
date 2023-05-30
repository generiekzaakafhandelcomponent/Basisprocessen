package com.ritense.valtimo.common.autoconfigure

import com.ritense.document.service.DocumentService
import com.ritense.openzaak.service.ZaakResultaatService
import com.ritense.openzaak.service.ZaakService
import com.ritense.openzaak.service.ZaakStatusService
import com.ritense.openzaak.service.impl.OpenZaakConfigService
import com.ritense.openzaak.service.impl.OpenZaakTokenGeneratorService
import com.ritense.zakenapi.link.ZaakInstanceLinkService
import com.ritense.valtimo.common.client.OpenzaakClient
import com.ritense.valtimo.common.service.ZaakPropertiesService
import com.ritense.valtimo.common.service.ZaakHandleService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonAutoConfiguration {

    @Bean
    fun zaakPropertiesService(
        zaakService: ZaakService,
        documentService: DocumentService
    ): ZaakPropertiesService {
        return ZaakPropertiesService(
            zaakService,
            documentService
        )
    }

    @Bean
    fun openzaakClient(
        openZaakConfigService: OpenZaakConfigService,
        openZaakTokenGeneratorService: OpenZaakTokenGeneratorService
    ): OpenzaakClient {
        return OpenzaakClient(openZaakConfigService, openZaakTokenGeneratorService)
    }

    @Bean
    fun zaakHandleService(
        zaakInstanceLinkService: ZaakInstanceLinkService,
        openzaakClient: OpenzaakClient,
        zaakService: ZaakService,
        zaakStatusService: ZaakStatusService,
        zaakResultaatService: ZaakResultaatService,
        documentService: DocumentService
    ): ZaakHandleService {
        return ZaakHandleService(
            zaakInstanceLinkService,
            openzaakClient,
            zaakService,
            zaakStatusService,
            documentService,
            zaakResultaatService
        )
    }
}