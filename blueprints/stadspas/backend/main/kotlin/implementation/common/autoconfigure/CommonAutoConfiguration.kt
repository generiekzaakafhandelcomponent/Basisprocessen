package implementation.common.autoconfigure

import com.ritense.besluit.service.BesluitService
import com.ritense.connector.service.ConnectorService
import com.ritense.document.service.DocumentService
import com.ritense.openzaak.service.ZaakResultaatService
import com.ritense.openzaak.service.ZaakService
import com.ritense.openzaak.service.ZaakStatusService
import com.ritense.openzaak.service.impl.OpenZaakConfigService
import com.ritense.openzaak.service.impl.OpenZaakTokenGeneratorService
import com.ritense.openzaak.service.impl.ZaakInstanceLinkService
import com.ritense.processdocument.service.ProcessDocumentAssociationService
import com.ritense.resource.service.OpenZaakService
import implementation.common.client.OpenzaakClient
import implementation.common.service.ActiveProcessCheckService
import implementation.common.service.MessageCorrelationService
import implementation.common.service.ObjectsApiService
import implementation.common.service.ZaakPropertiesService
import implementation.common.service.ZaakHandleService
import implementation.common.service.DocumentWriterService
import implementation.common.service.BesluitCreateService
import implementation.common.service.DateTimeService
import implementation.common.service.DocumentReaderService
import implementation.common.service.JobService
import org.camunda.bpm.engine.RuntimeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonAutoConfiguration {

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
        openZaakService: OpenZaakService
    ): BesluitCreateService {
        return BesluitCreateService(
            zaakInstanceLinkService,
            documentService,
            connectorService,
            besluitService,
            openZaakService
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
}
