package implementation.stadspas.autoconfigure

import com.ritense.connector.service.ConnectorService
import com.ritense.document.service.DocumentService
import implementation.common.service.DateTimeService
import implementation.common.service.DocumentReaderService
import implementation.stadspas.mapper.StadspasAanvraagMapper
import implementation.stadspas.service.StadspasCasePropertiesInitService
import implementation.stadspas.service.StadspaspasProcessService
import implementation.stadspas.service.StadspasProductObjectsApiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StadspasAutoConfiguration {

    @Bean
    fun stadspasProductObjectsApiService(
        connectorService: ConnectorService,
        documentService: DocumentService
    ): StadspasProductObjectsApiService {
        return StadspasProductObjectsApiService(
            connectorService,
            documentService
        )
    }

    @Bean
    fun stadspasProcessService(
        documentService: DocumentService,
        dateTimeService: DateTimeService,
        @Value("\${implementation.stadspas.besluitType: }") besluitTypeOmschrijving: String
    ): StadspasProcessService {
        return StadspasProcessService(
            documentService,
            dateTimeService,
            besluitTypeOmschrijving
        )
    }
    @Bean
    fun stadspasAanvraagMapper(
        documentService: DocumentService,
        documentReaderService: DocumentReaderService
    ): StadspasAanvraagMapper {
        return StadspasAanvraagMapper(documentService, documentReaderService)
    }

    @Bean
    fun stadspasOpgegevenDataToBeoordelingService(
        documentService: DocumentService
    ): StadspasOpgegevenDataToBeoordelingService {
        return stadspasOpgegevenDataToBeoordelingService(documentService)
    }
}
