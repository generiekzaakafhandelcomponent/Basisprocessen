// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
import {NgxLoggerLevel} from 'ngx-logger';
import {ValtimoConfig, UploadProvider} from '@valtimo/config';
import {authenticationKeycloak} from './auth/keycloak-config.dev';
import {openZaakExtensionInitializer} from '@valtimo/open-zaak';
import {menuItems} from './menu/menu';
import {defaultDefinitionColumns} from './definition/defaultDefinitionColumns';
import {customDefinitionTables} from './definition/customDefinitionsTables';
import {connectorLinkExtensionInitializer} from '@valtimo/connector-management';

export const environment: ValtimoConfig = {
  production: false,
  initializers: [
    openZaakExtensionInitializer,
    connectorLinkExtensionInitializer
  ],
  authentication: authenticationKeycloak,
  menu: {
    menuItems: menuItems
  },
  whitelistedDomains: [window['env']['whiteListedDomain'], 'localhost:4200'],
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
  featureToggles: {
    disableFormFlow: true,
    showUserNameInTopBar: true
  },
  openZaak: {
    catalogus: window['env']['openZaakCatalogusId'] || ''
  },
  uploadProvider: UploadProvider.OPEN_ZAAK,
  defaultDefinitionTable: defaultDefinitionColumns,
  customDefinitionTables: customDefinitionTables,
  translationResources: [
    {prefix: "./assets/i18n/", suffix: ".json"},
  ]
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
