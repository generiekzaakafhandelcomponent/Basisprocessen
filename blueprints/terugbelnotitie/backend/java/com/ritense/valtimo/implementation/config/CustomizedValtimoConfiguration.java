package com.ritense.valtimo.implementation.config;

import com.ritense.document.autoconfigure.DocumentAutoConfiguration;
import com.ritense.document.service.SearchFieldService;
import com.ritense.document.service.impl.JsonSchemaDocumentSearchService;
import com.ritense.valtimo.contract.database.QueryDialectHelper;
import com.ritense.valtimo.implementation.service.CustomDocumentSearchService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;

import javax.persistence.EntityManager;

@Configuration
@AutoConfigureBefore(DocumentAutoConfiguration.class)
public class CustomizedValtimoConfiguration {

    // This bean can be removed when switching to Valtimo Major 10
    @Bean
    public JsonSchemaDocumentSearchService documentSearchService(
        final EntityManager entityManager,
        final QueryDialectHelper queryDialectHelper,
        final SearchFieldService searchFieldService,
        final UserManagementService userManagementService
    ) {
        return new CustomDocumentSearchService(
            entityManager,
            queryDialectHelper,
            searchFieldService,
            userManagementService
        );
    }
}