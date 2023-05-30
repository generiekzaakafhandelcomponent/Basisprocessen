package com.ritense.valtimo.common.autoconfigure

import com.ritense.document.service.DocumentService
import com.ritense.valtimo.common.service.DateTimeService
import com.ritense.valtimo.common.service.DocumentReaderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonAutoConfiguration {

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
}