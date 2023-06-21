package com.implementation.common.autoconfigure

import com.implementation.common.client.OpenzaakClient
import com.implementation.common.service.ZaakHandleService
import com.implementation.common.service.ZaakPropertiesService
import com.ritense.document.service.DocumentService
import com.ritense.openzaak.service.ZaakResultaatService
import com.ritense.openzaak.service.ZaakService
import com.ritense.openzaak.service.ZaakStatusService
import com.ritense.openzaak.service.impl.OpenZaakConfigService
import com.ritense.openzaak.service.impl.OpenZaakTokenGeneratorService
import com.ritense.zakenapi.link.ZaakInstanceLinkService
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