# ValtimoAngularConsole

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 7.1.4.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## Docker

To start up this project with docker, make sure a production build is present in the [deployment/dist](./deployment/dist/). If it is not present, use `npm run buildProd`

Next, build the Docker image with the following command:

`docker build -f nginx.Dockerfile -t ritense/valtimo-angular-console:latest .`

This image can be started on port 4200 using the following command:

`docker run -e API_URI="http://localhost:8080/api/" -e MOCK_API_URI="http://localhost:8080/mock-api/" -e SWAGGER_URI="http://localhost:8080/v2/api-docs" -e KEYCLOAK_URL="http://localhost:8082/auth" -e KEYCLOAK_REALM="valtimo" -e KEYCLOAK_CLIENT_ID="valtimo-console" -e KEYCLOAK_REDIRECT_URI="http://localhost:4200" -e KEYCLOAK_LOGOUT_REDIRECT_URI="http://localhost:4200" -e WHITELISTED_DOMAIN="localhost:4200" -p 4200:80 ritense/valtimo-angular-console`

The environment variables in this sample command can be replaced by your own values. Make sure all variables present in the file [config.template.js](./assets/config.template.js) are specified.

