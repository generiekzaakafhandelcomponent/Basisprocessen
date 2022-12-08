# Generieke zaak
This is a generic process for handling a case (zaak) in municipalities.

It automatically creates a case in OpenZaak when a product has been requested by a resident. It handles the creation of communication, document creation and decision making. The final product is stored and send to the resident.

This blueprint was tested on Valtimo `9.24.0.RELEASE`.

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
Some tasks in the model have an empty implementation. These rely on form or process-links (FormConnectors or Plugins)
to perform a task. Detailed documentation can be found [here](https://docs.valtimo.nl/using-valtimo)

The following changes need to be made in the web-interface of GZAC:
* `Genereren brief` uses the SmartDocuments- or any other document plugin.
* `Opslaan Document in Documenten API` uses the Documenten API.
* `Linken Document aan Zaak` uses 'Link document to zaak' action on the Zaken API.
* `Behandelen zaak` should already be linked to the `generiek-proces.behandelen-zaak` form at deployment.
* `Burger taak: Upload geldig legitimatiebewijs` No form has been provided. A custom form should be created.
* `Maak besluit` uses the Besluiten connector with action `Create besluit`.
* `Zet zaak resultaat` uses the Besluiten connector with action `Set resultaat`.
