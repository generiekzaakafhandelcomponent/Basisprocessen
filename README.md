# Process blueprints

[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/generiekzaakafhandelcomponent/Basisprocessen/blob/feature/generieke-zaak/README.md)

Process blueprints are a collection of files, that either form a starting point to speed up development for an
implementation. There are two kinds of blueprints. Process blueprints that directly add value (e.g. a blueprint for City
Pass applications), and process blueprints that are a part of the bigger picture (e.g. a process on handling change
requests for a case). These can consist of one or more BPMNs, a document definition, forms, as well as less common
things such as custom front-end and back-end code.

## How to import a process blueprint

Importing a process blueprint and customizing it for a specific use case can be done by following the instructions on
this page. Additionally, specific instructions for a blueprint can be found on GitHub for that particular process
blueprint. These always take precedent over these general instructions.

### Copying files

Almost all the files in `backend` can be copied into the back-end of an implementation project. The exceptions are:

* `application.yml`. This only includes the configurations required by the process blueprint. These should be merged
  with the `application.yml` you already have.
* `build.gradle` or `pom.xml`. This only includes the dependencies required by the process blueprint. These should be
  merged with the `build.gradle` or `pom.xml` you already have.

Almost all files in `frontend` can be copied into the front-end of an implementation project. The exception is
`package.json`. This only includes the dependencies required by the process blueprint. These should be merged with
the `package.json` you already have.

### Configuring and customizing the process blueprint

A process blueprint will not work out of the box. Plugins might need to be configured, and process links need to be set.
Each process blueprint comes with a README that explains the specifics that should be configured in order for the
process blueprint to be used. In addition to this, consider the following checklist:

* Check each task in the BPMN definition. Are all tasks defined correctly?
* If a task relies on a method for a specific spring bean, are both the bean and the method present?
* Does this process blueprint rely on plugins? If so, these will have to be configured, and process links will have to
  be configured for the appropriate tasks as well.
* Does every user task have a form or form flow associated with it?
* If there are form flows, are all the forms it references present?

> Be sure to follow the specific instructions that are included in the README of the process blueprint.

Instructions on how to contribute to this repository can be found [here](https://github.com/generiekzaakafhandelcomponent/Basisprocessen/blob/feature/generieke-zaak/CONTRIBUTING.md).
