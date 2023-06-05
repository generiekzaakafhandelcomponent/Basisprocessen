package implementation.common.autoconfigure

import com.ritense.document.service.DocumentService
import implementation.common.service.DateTimeService
import implementation.common.service.DocumentReaderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonAutoConfiguration {
    @Bean
    fun dateTimeService(): DateTimeService {
        return DateTimeService()
    }
}
