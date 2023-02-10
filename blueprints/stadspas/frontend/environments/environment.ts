// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
import {NgxLoggerLevel} from 'ngx-logger';
import {authenticationKeycloak} from './auth/keycloak-config.dev';
import {openZaakExtensionInitializer} from '@valtimo/open-zaak';
import {customDefinitionColumns} from './definition/customDefinitionColumnsConfig';
import {menuItems} from './menu/menu';
import {defaultDefinitionColumns} from './definition/defaultDefinitionColumnsConfig';
import {connectorLinkExtensionInitializer} from '@valtimo/connector-management';
import {UploadProvider, ValtimoConfig, DossierListTab} from '@valtimo/config';
import {LOGO_BASE_64} from './logo';

export const environment: ValtimoConfig = {
    logoSvgBase64: LOGO_BASE_64,
    applicationTitle: '',
    production: false,
    initializers: [
        openZaakExtensionInitializer,
        connectorLinkExtensionInitializer
    ],
    authentication: authenticationKeycloak,
    menu: {
        menuItems: menuItems
    },
    whitelistedDomains: ['localhost:4200'],
    mockApi: {
        endpointUri: window['env']['swaggerUri'] || '/mock-api/'
    },
    valtimoApi: {
        endpointUri: window['env']['mockApiUri'] || '/api/'
    },
    swagger: {
        endpointUri: window['env']['apiUri'] || '/v2/api-docs'
    },
    logger: {
        level: NgxLoggerLevel.TRACE
    },
    definitions: {
        dossiers: []
    },
    openZaak: {
        catalogus: window['env']['openZaakCatalogusId'] || 'openZaakCatalogusId'
    },
    featureToggles: {
        disableFormFlow: true,
        showUserNameInTopBar: true,
        caseSearchFields: true,
        disableCaseCount: false
    },
    visibleDossierListTabs: [
        DossierListTab.ALL,
        DossierListTab.MINE,
        DossierListTab.OPEN
    ],
    uploadProvider: UploadProvider.DOCUMENTEN_API,
    defaultDefinitionTable: defaultDefinitionColumns,
    customDefinitionTables: customDefinitionColumns,
    translationResources: ['./assets/i18n']
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
