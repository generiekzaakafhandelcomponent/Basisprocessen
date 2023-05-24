# Informeren aanvrager
## Prerequisites
- A working Valtimo instance

## Components

- AutoConfig
- Services
    - DateTimeService
    - DocumentReaderService
- Process model
- Document schema
- Form
- Form-Link
- Process-Document-Link

## Configuration
- Make sure you have a properties file to the project root. This file should have the name ".env.properties"
- Add the files from the blueprint into the project root
- If you already have an AutoConfiguration file, merge the code into your existing file.

## Starting up
- Run the following command from a terminal in the project root: ```./gradlew bootrun```
- After the Valtimo backend application has finished starting up, the service is available at http://localhost:8080
  - The application uptime can be verified by calling [this API endpoint](http://localhost:8080/api/ping)

## Supporting containers
The Valtimo backend application requires a Keycloak instance and a database server. When running the application locally, running Keycloak and the database locally as well is recommended. In the [valtimo-docker-compose repository](https://github.com/valtimo-platform/valtimo-docker-compose), Docker compose files are available to support the Valtimo application. This repository includes a guide on which Compose file to use.

This blueprint was tested on Valtimo `10.5.0.RELEASE`.

