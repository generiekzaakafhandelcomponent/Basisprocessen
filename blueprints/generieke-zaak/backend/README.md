# Generieke zaak
This is a generic process for handling a case (zaak) in municipalities.

It automatically creates a case in OpenZaak when a product has been requested by a resident. It handles the creation of communication, document creation and decision making. The final product is stored and send to the resident. 

## Prerequisites
* A working GZAC implementation
* Camunda modeler (https://camunda.com/download/modeler/)

### Gradle dependencies

* `com.ritense.valtimo:form`
* `com.ritense.valtimo:form-link`
* `com.ritense.valtimo:besluit`
* `com.ritense.valtimo:klant`
* `com.ritense.valtimo:openzaak`
* `com.ritense.valtimo:wordpress-mail`
* `com.ritense.valtimo:connector`
* `com.ritense.valtimo:contactmoment`
* `com.ritense.valtimo:smartdocuments`

## Adjusting the BPMN models
Please use the Camunda modeler to make the changes as described below:  
* `resources/bpmn/generiek-proces.bpmn`
  * Change properties on `Versturen email notificatie` task:
    * `mailSendTaskFrom`
    * `mailSendTaskSubject`
    * `mailSendTaskTemplate`
* `resources/bpmn/zaakstatus-update.bpmn`
  * Change properties on `Versturen email notificatie` task:
    * `mailSendTaskFrom`
    * `mailSendTaskSubject`
    * `mailSendTaskTemplate`

## Configuring the process links
Some task in the model have an empty implementation. These rely on form or process-links (FormConnectors or Plugins) to perform a task. Detailed documentation can be found [here](https://docs.valtimo.nl/using-valtimo)

The following changes need to be made in in web-interface of GZAC:
* `Genereren brief` uses the SmartDocuments- or any other document plugin
* `Opslaan Document in Documenten API` uses the Documenten API
* `Linken Document aan Zaak` uses 'Link document to zaak' action on the Zaken API
* `Behandelen zaak` should already be linked to the `generiek-proces.behandelen-zaak` form at deployment
* `Burger taak: Upload geldig legitimatiebewijs` No form has been provided. A custom form should be created.
* `Maak besluit` uses the Besluiten connector with action `Create besluit`
* `Zet zaak resultaat` uses the Besluiten connector with action `Set resultaat`
