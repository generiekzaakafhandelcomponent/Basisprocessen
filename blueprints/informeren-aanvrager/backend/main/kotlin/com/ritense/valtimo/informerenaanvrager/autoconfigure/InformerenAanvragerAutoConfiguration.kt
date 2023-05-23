package com.ritense.valtimo.informerenaanvrager.autoconfigure

import com.ritense.besluit.service.BesluitService
import com.ritense.connector.service.ConnectorService
import com.ritense.document.service.DocumentService
import com.ritense.objectmanagement.repository.ObjectManagementRepository
import com.ritense.openzaak.service.ZaakResultaatService
import com.ritense.openzaak.service.ZaakService
import com.ritense.openzaak.service.ZaakStatusService
import com.ritense.openzaak.service.impl.OpenZaakConfigService
import com.ritense.openzaak.service.impl.OpenZaakTokenGeneratorService
import com.ritense.plugin.service.PluginService
import com.ritense.zakenapi.link.ZaakInstanceLinkService
import com.ritense.processdocument.service.ProcessDocumentAssociationService
import com.ritense.resource.service.OpenZaakService
import com.ritense.valtimo.common.client.OpenzaakClient
import com.ritense.valtimo.common.service.ActiveProcessCheckService
import com.ritense.valtimo.common.service.MessageCorrelationService
import com.ritense.valtimo.common.service.ObjectsApiService
import com.ritense.valtimo.common.service.ZaakPropertiesService
import com.ritense.valtimo.common.service.ZaakHandleService
import com.ritense.valtimo.common.service.DocumentWriterService
import com.ritense.valtimo.common.service.BesluitCreateService
import com.ritense.valtimo.common.service.DateTimeService
import com.ritense.valtimo.common.service.DocumentReaderService
import com.ritense.valtimo.common.service.JobService
import com.ritense.valtimo.common.service.CustomObjectenApiService
import com.ritense.valtimo.common.service.CustomZaakObjectService
import org.camunda.bpm.engine.RuntimeService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InformerenAanvragerAutoConfiguration {

    @Bean
    fun messageCorrelationService(
        runtimeService: RuntimeService,
        documentService: DocumentService,
        associationService: ProcessDocumentAssociationService
    ): MessageCorrelationService {
        return MessageCorrelationService(
            runtimeService,
            documentService,
            associationService
        )
    }


    fun activeProcessCheckService(
        runtimeService: RuntimeService
    ): ActiveProcessCheckService {
        return ActiveProcessCheckService(
            runtimeService
        )
    }

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

    @Bean
    fun besluitCreateService(
        zaakInstanceLinkService: ZaakInstanceLinkService,
        documentService: DocumentService,
        connectorService: ConnectorService,
        besluitService: BesluitService,
        openZaakService: OpenZaakService,
        @Value("\${valtimo.besluitDocumentRequired: }") besluitDocumentRequired: Boolean
    ): BesluitCreateService {
        return BesluitCreateService(
            zaakInstanceLinkService,
            documentService,
            connectorService,
            besluitService,
            openZaakService,
            besluitDocumentRequired
        )
    }

    @Bean
    fun documentWriterService(
        documentService: DocumentService
    ): DocumentWriterService {
        return DocumentWriterService(
            documentService
        )
    }

    @Bean
    fun documentReaderService(
        documentService: DocumentService
    ): DocumentReaderService {
        return DocumentReaderService(
            documentService
        )
    }

    @Bean
    fun dateTimeService(): DateTimeService {
        return DateTimeService()
    }

    @Bean
    fun jobService(): JobService {
        return JobService()
    }

    @Bean
    fun customZaakObjectService(
        openZaakConfigService: OpenZaakConfigService,
        openZaakTokenGeneratorService: OpenZaakTokenGeneratorService,
        zaakService: ZaakService
    ): CustomZaakObjectService {
        return CustomZaakObjectService(
            openZaakConfigService,
            openZaakTokenGeneratorService,
            zaakService
        )
    }

    @Bean
    fun customObjectenApiService(
        objectManagementRepository: ObjectManagementRepository,
        pluginService: PluginService
    ): CustomObjectenApiService {
        return CustomObjectenApiService(
            objectManagementRepository,
            pluginService
        )
    }
}
