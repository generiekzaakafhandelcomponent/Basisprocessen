package com.ritense.valtimo.implementation.autoconfigure

import com.ritense.valtimo.contract.config.LiquibaseMasterChangeLogLocation
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import javax.sql.DataSource

@Configuration
class LiquibaseConfiguration {

    @Order
    @Bean
    @ConditionalOnMissingBean(name = ["exampleLiquibaseMasterChangeLogLocation"])
    fun exampleLiquibaseMasterChangeLogLocation(): LiquibaseMasterChangeLogLocation {
        return LiquibaseMasterChangeLogLocation("config/liquibase/master.xml")
    }

}
