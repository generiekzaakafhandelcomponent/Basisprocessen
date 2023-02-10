// tslint:disable-next-line:max-line-length
import {KeycloakAuthGuardService, keycloakInitializer, KeycloakModule, KeycloakUserService, ValtimoKeycloakOptions} from '@valtimo/keycloak';
import {KeycloakConfig, KeycloakOnLoad} from 'keycloak-js';
import {Injector} from '@angular/core';
import {Auth, AuthProviders} from '@valtimo/config';

const keycloakAuthenticationProviders: AuthProviders = {
  guardServiceProvider: KeycloakAuthGuardService,
  userServiceProvider: KeycloakUserService
};

const keycloakConfigDev: KeycloakConfig = {
  url: window['env']['keycloakUrl'] || 'keycloakUrl',
  realm: window['env']['keycloakRealm'] || 'keycloakRealm',
  clientId: window['env']['keycloakClientId'] || 'keycloakClientId'
};

const keycloakOnLoad: KeycloakOnLoad = 'login-required';

const keycloakInitOptions: any = {
  config: keycloakConfigDev,
  onLoad: keycloakOnLoad,
  checkLoginIframe: false,
  flow: 'standard',
  redirectUri: window['env']['keycloakRedirectUri'] || 'keycloakRedirectUri'
};

const valtimoKeycloakOptions: ValtimoKeycloakOptions = {
  keycloakOptions: {
    config: keycloakConfigDev,
    initOptions: keycloakInitOptions,
    enableBearerInterceptor: true,
    bearerExcludedUrls: [
      '/assets'
    ]
  },
  logoutRedirectUri: window['env']['keycloakLogoutRedirectUri'] || 'http://localhost:4200'
};

export function initializerKeycloak(injector: Injector) {
  return keycloakInitializer(injector);
}

export const authenticationKeycloak: Auth = {
  module: KeycloakModule,
  initializer: initializerKeycloak,
  authProviders: keycloakAuthenticationProviders,
  options: valtimoKeycloakOptions
};
