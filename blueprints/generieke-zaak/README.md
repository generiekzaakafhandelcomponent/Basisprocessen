# Generieke zaak
This is a generic process for handling a case (zaak) in municipalities.

It automatically creates a case in OpenZaak when a product has been requested by a resident. It handles the creation of communication, document creation and decision making. The final product is stored and send to the resident.

This blueprint was tested on Valtimo `9.24.0.RELEASE`.

**Included process models:**
<details>
  <summary>Generiek proces</summary>
  <img src="generiek-proces.png" alt="Generiek proces"/>
</details>
<details>
  <summary>Zet zaak status</summary>
  <img src="zaakstatus-update.png" alt="Zet zaak status"/>
</details>


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

**Generiek proces**
* `Genereren brief` uses the `SmartDocuments - Document genereren`.
* `Opslaan Document in Documenten API` uses the `Documenten API - Document opslaan`.
* `Linken Document aan Zaak` uses 'Link document to zaak' action on the `Zaken API - Koppel document aan zaak`.
* `Behandelen zaak` should already be linked to the `generiek-proces.behandelen-zaak` form at deployment.
* `Burger taak: Upload geldig legitimatiebewijs` No form has been provided. A custom form should be created.
* `Maak besluit` uses the OpenZaak connector with action `Create besluit`.
* `Zet zaak resultaat` uses the OpenZaak connector with action `Set resultaat`.
