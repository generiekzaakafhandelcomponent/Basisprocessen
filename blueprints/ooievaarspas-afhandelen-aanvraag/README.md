
# Ooievaarspas afhandelen aanvraag
Dit herbruikbare proces is gemaakt voor de Nederlandse gemeente Den Haag om de aanvraag voor de Ooievaarspas te verwerken (Dit is een sociaal programma voor de inwoners van Den Haag, dat korting geeft op allerlei activiteiten op het gebied van sport, cultuur, en entree).

Alle deelprocessen zijn ook beschikbaar in de repository Bouwblokken onder het GitHub-account van de generiekzaakafhandelcomponent (link: https://github.com/generiekzaakafhandelcomponent/Bouwblokken)


This blueprint was tested on Valtimo `?`.

**Included process models:**
<details>
  <summary>Ooievaarspas afhandelen aanvraag</summary>
  <img src="ooievaarspas-afhandelen-aanvraag.png" alt="ooievaarspas afhandelen aanvraag"/>
</details>


## Adjusting the BPMN models
Please use the Camunda modeler to make the changes as described below:  
* `?`
  * Change properties on `?` task:
    * `?`
    * `?`


## Configuring the case dependencies
The blueprint requires some post-deployment configuration. This is described below.

Documentation about this subject can be found [here](https://docs.valtimo.nl/using-valtimo)
### Connectors
Connectors can be found and configured at Admin -> Connectors.
* **?**
  * ?
    * ?


### Plugins
Documentation about plugins can be found [here](https://docs.valtimo.nl/using-valtimo/plugins)
* [**SmartDocuments**](https://docs.valtimo.nl/using-valtimo/plugins/configure-smartdocuments-plugin#configure-the-plugin)
* [**OpenZaak**](https://docs.valtimo.nl/using-valtimo/plugins/configure-openzaak-plugin#configuring-the-plugin)
* [**Documenten API**](https://docs.valtimo.nl/using-valtimo/plugins/configure-documenten-api-plugin#configure-the-plugin)
* **Catalogi API**
  * Please enter a name, the URL of the Catalogi API and select the OpenZaak configuration created earlier
* [**Zaken API**](https://docs.valtimo.nl/using-valtimo/plugins/configure-zaken-api-plugin#configure-the-plugin)


### Process links (Connectors)
**Generiek proces**
* ?

### Process links (v2, Plugins)
* ?

### Process walk through
Het proces verloopt als volgt:
- De organisatie krijgt een verzoek, hierdoor start het proces
- De aanvraag wordt automatisch in het systeem ingevoerd
- Er wordt een ontvangstbevestiging gegenereerd en een bevestiging wordt naar de aanvrager gestuurd (subproces)
- De casusstatus wordt bijgewerkt (subproces)
- De zaak wordt toegewezen aan een medewerker, de zaakstatus wordt bijgewerkt
- De medewerker handelt de aanvraag af (subproces)
- Als de beslissing niet is genomen, wordt de aanvrager om meer informatie gevraagd
- Als de beslissing is genomen, wordt deze gedocumenteerd en gearchiveerd
- De aanvrager krijgt een melding (subproces)
- De zaak wordt gesloten
- Indien dit niet binnen 7 weken is afgehandeld, wordt door het systeem een ​​calamiteitenstatus geactiveerd

### Forms
* ?

### Other
* ?