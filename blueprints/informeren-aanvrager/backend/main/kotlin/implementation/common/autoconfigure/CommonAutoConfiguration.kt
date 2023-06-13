package implementation.common.autoconfigure

import implementation.common.service.DateTimeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonAutoConfiguration {
    @Bean
    fun dateTimeService(): DateTimeService {
        return DateTimeService()
    }
}
