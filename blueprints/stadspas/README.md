# Aanvraag  stadspas
**Stadspas (eng: city discount pass)**:
The 'Stadspas' is a city discount pass that the Dutch municipalities provide to the inhabitants that are below a certain income threshold. This pass will provide the holder all kinds of discounts to services provided by the city itself or other partners.

**These processes support:**

* The processing of an application for an ‘Ooievaarspas’ by a inhabitant of the city of The Hague

* The processing of a request for information from the applicant by a case handler either via the customer portal (nl: 'klantportaal') or via post.

* The processing of a formal decision (nl: ‘besluit’) on the application by the case handler

* The creation of the product ‘Ooievaarspas’ as an object in the objects api

* Generation of the necessary emails and documents.

**Afhandelen Aanvraag**

* Message catch event ‘Product aanvraag ontvangen’ is the trigger for the start of the proces ‘Afhandelen aanvraag’. This event is preceded by the following activities within the ZGW platform:

  * An applicant is able to complete an application for an ‘Ooievaarspas’ (Openforms form which has been developed internally by The Hague) on the website of the municipality of The Hague.

  * The data of the form is submitted as an object of the type ‘Productaanvraag’ in the objects api. As a response, the objects api puts a notification on the Open notification message bus.

  * GZAC has a subscription on these notifications and as a response, it will pull the ‘productaanvraag’ data from the objects api and create a case of the type ‘Aanvraag Opas (ooievaarspas)’ with the help of Ooievaarspas productaanvraag connector.

  * GZAC deletes the ‘Productaanvraag’ object from the objects api.

* The service task ‘Vertalen ontvangen data naar document' of 'Ooievaarspas: Afhandelen aanvraag’ process converts and saves the received productaanvraag data to the document.

* The service task ‘Kopiëren op gegeven data naar beoordeling’ duplicates the application data to the ‘Beoordeling’ keys in document. This enables the creation of a report at the end of the process, which compares data received from the applicant with data updated by the case handler.

* The service task ‘Zet aanvraag ontvangen datum naar document’

* The service task ‘Genereren ontvangstbevestiging’ is not implemented for this proces and therefore skipped in the process.

* The service task ‘Zet default communicatievoorkeur’ sets the communicatievoorkeur to email, so all correspondence is done by e-mail unless it is changed by the case handlers within the process.

* The call activity ‘Informeren aanvrager’ is used to determine via which channel (email, post, portal) the applicant is informed of the receipt of the application. More information on this can be found below under building blocks.

* The call activity ‘Update zaakstatus’ is used to set the zaakstatus to ‘aanvraag ontvangen’. More information on this can be found below under building blocks.

* The user Task ‘Bepalen zaakbehandelaar’ is meant to trigger the case handler to assign a case assignee (nl: zaakbehandelaar) to the case by either using the ‘claim’ button or by clicking the link 'Gebruiker toewijzen aan dit dossier' (eng: 'Assign a user to this case').

* The call activity ‘Update zaakstatus’ is used to set the zaakstatus to ‘In behandeling genomen’. More information on this can be found below under building blocks.

* The call activity ‘Beoordelen afhandelen aanvraag form flow’ starts the form-sequence steps, a sequence of user tasks in which the case handler assesses and - if needed - updates the information provided by the applicant. At the end, he can either send out a request for information (nl: informatieverzoek) or make a decision (besluit).
  * Case handler chooses to send out a request for information:

    * The call activity ‘Genereren document’ generates a letter which instructs the applicant to provide certain documents. More information on this can be found below under building blocks.

    * The call activity ‘Opvragen informatie’ processes the response of the request for information. More information on this can be found below under building blocks.

    * The call activity ‘Controleren afhandeltermijn’ calculates the period between the moment the request for information was done and the moment the escalation took place due to no response from the applicant. More information on this can be found below under building blocks.
    
  * Case handler makes a decision (one of the following three: Aanvraag toekenen, Aanvraag afwijzen, Aanvraag buiten behandeling stellen)

    * The call activity ‘genereer document’ generates the report ‘Rapportage Afhandeling aanvraag ooievaarspas.pdf’, saves it to Documenten api and links it to the case in openzaak. More information on this can be found below under building blocks.

    * The DMN table ‘Bepalen beschikking template’ determines which smart documents template needs to be used to generate the ‘beschikking’ document based on the made decision.

    * The call activity ‘genereer document’ generates the ‘beschikking’ document, saves it to Documenten api and links it to the case in openzaak. More information on this can be found below under building blocks.

    * Service taks ‘Vastleggen besluit’ creates a ‘besluit’ in openzaak, links the generated ‘beschikking’ document to the ‘besluit’ and links the ‘besluit’ to the case in openzaak.

    * The DMN table ‘Bepaal zaakresultaat’ determines which result needs to be set in openzaak based on the made decision.

    * Service task ‘vastleggen product’ creates an object of the objecttype ‘product stadspas' in the objects api when the decision is ‘Aanvraag toekenen’

    * Service task ‘Koppelen product aan zaak’ links the created object in the objects api with the case in openzaak

    * The call activity ‘Informeren aanvrager’ is used to determine via which channel (email, post, portal) the applicant is informed of the made decision. More information on this can be found below under building blocks.

    * The call activity ‘Afsluiten zaak’ sets the case result in openzaak. This completes the case in GZAC.

* Service task ‘Zet escalatie vlag’ sets a value in the documents which triggers the appearance of an escalation flag (❗️❗️❗) on both the summary page and the case lists view to indicate to the case handler that this case needs attention.

* Message catch event ‘Verzoek om datum te wijzigen’ is triggered by throwing cath event with the proces ‘Controleren afhandeltermijn’.

* Service task ‘Update timer afhandeltermijn’ updates the non-interupting boundry timer event on the main proces.

**Connections with other systems**

OpenZaak, Objects-api, Objecttypes-api, Open-notifications, Open-forms, Smart documents, Wordpress mail.


**Prerequisites**

**Passwords and secrets**

As we don't want to commit secrets/passwords. A solution to work with secrets should already be there, either through the AWS parameter store or in Kubernetes with environment variables like below:

The following environment variables should be defined for Backend:

* VALTIMO_CONNECTOR-ENCRYPTION_SECRET= any secrets of 16 characters or its multiplies
* VALTIMO_JWT_SECRET= keycloak jwt secret
* VALTIMO_PLUGIN_ENCRYPTION-SECRET= any secrets of 16 characters or its multiplies
* CAMUNDA_BPM_ADMIN-USER_ID=any id
* KEYCLOAK_REALM= keyclaok realm
* KEYCLOAK_AUTH_SERVER_URL= keycloak auth url
* KEYCLOAK_RESOURCE= keycloak m2m client resource
* KEYCLOAK_CREDENTIALS_SECRET= keycloak credentials secret
* IMPLEMENTATION_STADSPAS_BESLUITTYPE=Besluit i.h.k.v. aanvraag stadspas

The following environment variables should be defined for Frontend:
* SWAGGER_URI
* MOCK_API_URI
* API_URI
* KEYCLOAK_URL
* KEYCLOAK_REALM
* KEYCLOAK_CLIENT_ID
* KEYCLOAK_REDIRECT_URI
* KEYCLOAK_LOGOUT_REDIRECT_URI
* WHITELISTED_DOMAIN
* BEARER_EXCLUDED_URLS
* OPEN_ZAAK_CATALOGUS


**Wordpress mail**

Make sure that the following templates are created and ready to use:
* ontvangstbevestiging-aanvraag-stadspas
* bewijsstukken-sturen
* besluit-aanvraag-stadspas

**Smart documents**

A user id (client id) and password are required to configure plug in GZAC and make sure that the following templates are available:
* Besluit over uw aanvraag Stadspas - Toekenning 1e keer
* Besluit over uw aanvraag Stadspas - Afwijzing
* Aanvraag Stadspas buiten behandeling
* Gegevens nodig aanvraag Stadspas
* Gegevens Stadspas ontvangen
* Rapportage Afhandeling aanvraag stadspas

**Objecten api and Objecttypen api**

Make sure that the following objecttypes are created in objecttypen api and they are available in objecten api.

* zaakdetails stadspas
* product stadspas
* productaanvraag stadspas
* formio
* taak

For every environment the formio object is unique, as it contains unique informatieobjecttypen. The json content of the formio can be found here onder:
```
{
    "formDefinition": {
        "display": "form",
        "settings": {
            "pdf": {
                "id": "1ec0f8ee-6685-5d98-a847-26f67b67dkk0",
                "src": "https://files.form.io/pdf/5692b91fd1028f01000407e3/file/1ec0f8ee-6685-5d98-a847-26f67b67dkk0"
            }
        },
        "components": [
            {
                "key": "informatieverzoeken",
                "type": "hidden",
                "input": true,
                "label": "Informatieverzoeken",
                "tableView": true
            },
            {
                "key": "html",
                "type": "htmlelement",
                "input": false,
                "label": "HTML",
                "content": "De volgende informatie dient nog aangeleverd te worden, voordat uw aanvraag in behandeling genomen kan worden:",
                "className": "alert alert-info",
                "tableView": false,
                "refreshOnChange": false
            },
            {
                "key": "toelichtingEnDeadline",
                "tag": "div",
                "type": "htmlelement",
                "input": true,
                "label": "Toelichting en deadline",
                "content": "<dl class=\"row mb-0\">\n <dd class=\"col-6\">Toelichting behandelaar</dd>\n <dt class=\"col-6\">{{ data.informatieverzoeken.at(-1).toelichting || '-' }}</dt></dl> <dl class=\"row mb-0\">\n <dd class=\"col-6\">Deadline</dd>\n <dt class=\"col-6\">{{ data.informatieverzoeken.at(-1).deadline ? moment(data.informatieverzoeken.at(-1).deadline).format('DD-MM-yyyy') : '-' }}</dt></dl>",
                "tableView": false
            },
            {
                "key": "partnerID",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van het identiteitsbewijs van uw partner",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.partnerID;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/IdentiteitsbewijsUuid"
            },
            {
                "key": "bankafschriftenAfgelopenMaand",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van de bankafschriften van de afgelopen maand",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.bankafschriftenAfgelopenMaand;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BankafschriftUuid"
            },
            {
                "key": "html",
                "type": "htmlelement",
                "input": false,
                "label": "HTML",
                "content": "Een kopie van de bankafschriften van de volgende IBAN rekeningen: {{ data.informatieverzoeken.at(-1).specifiekeBankrekeningNummers || ''}}",
                "tableView": false,
                "refreshOnChange": false,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.specifiekeBankafschriftenAfgelopenMaand;"
            },
            {
                "key": "specifiekeBankafschriftenAfgelopenMaand",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van de bankafschriften van de volgende IBAN rekeningen:",
                "hideLabel": "true",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.specifiekeBankafschriftenAfgelopenMaand;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BankafschriftUuid"
            },
            {
                "key": "html",
                "type": "htmlelement",
                "input": false,
                "label": "HTML",
                "content": "Een kopie van de bankafschriften van de volgende IBAN rekeningen: {{ data.informatieverzoeken.at(-1).bankrekeningNummersSpecifiekePeriode || ''}}",
                "tableView": false,
                "refreshOnChange": false,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.specifiekeBankafschriftenPerPeriode;"
            },
            {
                "key": "specifiekeBankafschriftenPerPeriode",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van de bankafschriften van de volgende IBAN rekeningen:",
                "hideLabel": "true",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.specifiekeBankafschriftenPerPeriode;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BankafschriftUuid"
            },
            {
                "key": "laatsteInkomstenSpecificatie",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van uw laatst ontvangen inkomensspecificatie (loonstrook)",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.laatsteInkomstenSpecificatie;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/InkomensspecificatieUuid"
            },
            {
                "key": "laatsteInkomstenSpecificatiePartner",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van de laatst ontvangen inkomensspecificatie (loonstrook) van uw partner",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.laatsteInkomstenSpecificatiePartner;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/InkomensspecificatieUuid"
            },
            {
                "key": "specificatiePensioen",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van de specificatie van uw pensioen",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.specificatiePensioen;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/PensioenOverzichtUuid"
            },
            {
                "key": "belastingaangifte",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van uw belastingaangifte afgelopen jaar",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.belastingaangifteAfgelopenJaar;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BelastingaangifteUuid"
            },
            {
                "key": "belastingaanslag",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van uw belastingaanslag van afgelopen jaar",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.belastingaanslagAfgelopenJaar;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BelastingaanslagUuid"
            },
            {
                "key": "jaarrekening",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van de Jaarrekening",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.jaarrekening;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/JaarrekeningUuid"
            },
            {
                "key": "balansrekening",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van de Balansrekening",
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.balansrekening;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BalansrekeningUuid"
            },
            {
                "key": "html",
                "type": "htmlelement",
                "input": false,
                "label": "HTML",
                "content": "Upload hieronder een kopie van het bewijs van inschrijving van de volgende personen: {{ data.informatieverzoeken.at(-1).opleidingVolgendePersonen || ''}}",
                "tableView": false,
                "refreshOnChange": false,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.bewijsVanInschrijving;"
            },
            {
                "key": "bewijsVanInschrijving",
                "type": "portalFileUpload",
                "input": true,
                "label": "Een kopie van het bewijs van inschrijving voor de opleiding:",
                "hideLabel": true,
                "tableView": false,
                "multipleFiles": true,
                "customConditional": "show = data.informatieverzoeken.at(-1).opTeVragenBenodigdeInformatie.bewijsVanInschrijving;",
                "informatieobjecttype": "https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BewijsVanInschrijvingUuid"
            },
            {
                "key": "reactie.ontvangenBestanden",
                "type": "hidden",
                "input": true,
                "label": "OntvangenBestanden",
                "tableView": false,
                "calculateValue": "value = data.partnerID?.concat(data.bankafschriftenAfgelopenMaand || [], data.specifiekeBankafschriftenAfgelopenMaand || [], data.specifiekeBankafschriftenPerPeriode || [], data.laatsteInkomstenSpecificatie || [], data.laatsteInkomstenSpecificatiePartner || [], data.specificatiePensioen || [], data.belastingaangifte || [], data.belastingaanslag || [], data.jaarrekening || [], data.balansrekening || [], data.bewijsVanInschrijving || []) || []"
            },
            {
                "key": "documenten",
                "type": "hidden",
                "input": true,
                "label": "Documenten",
                "tableView": false,
                "calculateValue":"value = []\nif (data.reactie?.ontvangenBestanden?.length > 0) {value.push('/reactie/ontvangenBestanden')}"
            },
            {
                "key": "reactie.reactiedatum",
                "type": "hidden",
                "input": true,
                "label": "Datum additionele informatie",
                "tableView": true,
                "customDefaultValue": "value = moment()"
            },
            {
                "key": "submit",
                "type": "button",
                "input": true,
                "label": "Doorgaan",
                "action": "custom",
                "custom": "delete data.informatieverzoeken; delete data.partnerID; delete data.bankafschriftenAfgelopenMaand; delete data.specifiekeBankafschriftenAfgelopenMaand; delete data.specifiekeBankafschriftenPerPeriode; delete data.laatsteInkomstenSpecificatie; delete data.laatsteInkomstenSpecificatiePartner; delete data.specificatiePensioen; delete data.belastingaangifte; delete data.belastingaanslag; delete data.jaarrekening; delete data.balansrekening; delete data.bewijsVanInschrijving; form.submit(); component.disabled = true;",
                "tableView": false,
                "showValidations": false,
                "disableOnInvalid": true
            }
        ]
    }
}
```

**OpenZaak**

Make sure that zaaktype 'Aanvraag stadspas behandelen' is created in openZaak with the following properties:

**Status**
* Afgehandeld
* Informatieverzoek uitgezet
* In behandeling genomen
* Aanvraag ontvangen

**Roltypen**
* Initiator
* Behandelaar

**Resultaattypen**
* Verstrekt
* Geweigerd
* Afgebroken
* Beëindigd

**Besluittypen**
* Besluit i.h.k.v. aanvraag stadspas

**Informatieobjecttypen**
* Aanvraag
* Besluit
* Bevestigingsbrief
* TermijnBrief
* Rapportage
* Bankafschrift
* Inkomensspecificatie
* PensioenOverzicht
* Identiteitsbewijs
* BewijsVanInschrijving
* Belastingaanslag
* Belastingaangifte
* Balansrekening
* Jaarrekening

**Open notification**

Make sure that a GZAC application is added and client id and secret is available in order to connect the connector and plugin in GZAC.

**Add connectors**
In **Admin > Connections** the following connectors should be added:

* OpenZaak
* Objects api
  * stadspas-zaakdossiersync
  * stadspas-product-stadspas
  * stadspas-productaanvraag
* Open notifications
* Productaanvraag
* Wordpress mail

**Add plugin**
In **Admin > Plugins** the following plugins should be added:

* Catalogi API
* Documenten API
* Notificaties API
* OpenNotificaties
* Object token authenticatie (Objecten API)
* Object token authenticatie (Objecttypen API)
* Objecten API
* Objecttypen API
* OpenZaak
* Portaaltaak
* SmartDocuments
* Zaken API

Note: visit the documentation https://docs.valtimo.nl/ to know how the plugins can be configured.

**Object management**
In **Admin > Objects** the following objects should be added:
* Taak
* Stadspas product
* Stadspas productaanvraag
* Stadspas zaakdetails

Taak object is mandatory as it is needed to configure portaal taak plugin. The other 3 are optional. Search fields and ‘show in menu’ options are out of scope for this MVP.

**Form links**

Form links with the user tasks are done in the backend. So it is not needed to do it in the UI.

**Process links**

Process links for openzaak service tasks is done via the backend code. So it is not needed to do it in the UI.

The following process links for plugins should be configured in Admin => Proceskoppelingen v2 (beta):

* 'Genereer document stadspas' process: configuration of the service tasks
  * Service task: Genereer document
       SmartDocuments plugin action -> document genereren
       ```
       Template-groep = GZAC
       Template-naam = pv:templateName
       Naam procesvariabele voor opslag document  = gegenereerdeStadspasDocument
       Documentformaat = PDF
       Template-data: The following key value pairs should be added:
       {
       "key": "openzaakIdentificatie",
       "value": "doc:/openzaak/identificatie"
       },
       {
       "key": "dagtekening",
       "value": "pv:dagtekeningBrief"
       },
       {
       "key": "datumAanvraag",
       "value": "doc:/datumAanvraagOntvangen"
       },
       {
       "key": "jaartal",
       "value": "pv:stadspasJaartal"
       },
       {
       "key": "redenAfwijzing",
       "value": "doc:/beoordelingEnAfhandeling/besluit/redenAfwijzing"
       },
       {
       "key": "datumDeadline",
       "value": "pv:deadlineInformatieverzoek"
       },
       {
       "key": "opTeVragenBenodigdeInformatie",
       "value": "pv:opTeVragenBenodigdeInformatie"
       },
       {
       "key": "specifiekeBankrekeningNummers",
       "value": "pv:specifiekeBankrekeningNummers"
       },
       {
       "key": "bankrekeningNummersSpecifiekePeriode",
       "value": "pv:bankrekeningNummersSpecifiekePeriode"
       },
       {
       "key": "stadspasHouders",
       "value": "doc:/beoordelingEnAfhandeling/besluit/stadspasToegekendePersonen"
       },
       {
       "key": "opleidingVolgendePersonen",
       "value": "pv:opleidingVolgendePersonen"
       },
       {
       "key": "datumInformatieverzoek",
       "value": "pv:datumInformatieverzoek"
       },
       {
       "key": "aanvrager",
       "value": "doc:/aanvrager"
       },
       {
       "key": "partner",
       "value": "doc:/partner"
       },
       {
       "key": "kinderen",
       "value": "doc:/kinderen"
       },
       {
       "key": "aanvraaggegevensOpgegeven",
       "value": "doc:/aanvraaggegevens"
       },
       {
       "key": "inkomenKlantOpgegeven",
       "value": "doc:/inkomenKlant"
       },
       {
       "key": "inkomenPartnerOpgegeven",
       "value": "doc:/inkomenPartner"
       },
       {
       "key": "vermogenOpgegeven",
       "value": "doc:/vermogen"
       },
       {
       "key": "beoordelingEnAfhandeling",
       "value": "doc:/beoordelingEnAfhandeling"
       }
       ```

  * Service task: Opslaan document in documenten api - Bevestigingbrief
    Documenten API -> Document opslaan
     ```
     Bestandsnaam = leave empty
     Vertrouwelijkheidsaanduiding = zaakvertrouwelijk
     Titel = Ontvangstbevestiging bewijsstukken or any titel
     Beschrijving = Ontvangstbevestiging bewijsstukken or any beschrijving
     Naam procesvariabele met document = gegenereerdeStadsDocument
     Naam procesvariabele voor opslag document-URL = gegenereerdeeStadspasDocumentUrl
     Taal = nl
     Status = Definitief
     URL naar het informatieobjecttype = https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BevestigingbriefUuid
     ```

  * Service task: Opslaan document in documenten api - Termijn brief
    Documenten API -> Document opslaan
     ```
     Bestandsnaam = leave empty
     Vertrouwelijkheidsaanduiding = zaakvertrouwelijk
     Titel = Brief bewijsstukken sturen or any titel
     Beschrijving = Brief bewijsstukken sturen or any beschrijving
     Naam procesvariabele met document = gegenereerdeStadspasDocument
     Naam procesvariabele voor opslag document-URL = gegenereerdeStadspasDocumentUrl
     Taal = nl
     Status = Definitief
     URL naar het informatieobjecttype = https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/TermijnBriefUuid
     ```

  * Service task: Opslaan document in documenten api - Besluit
    Documenten API -> Document opslaan
     ```
     Bestandsnaam = leave empty
     Vertrouwelijkheidsaanduiding = zaakvertrouwelijk
     Titel = Beschikking or any titel
     Beschrijving = Beschikking or any beschrijving
     Naam procesvariabele met document = gegenereerdeStadspasDocument
     Naam procesvariabele voor opslag document-URL = gegenereerdeStadspasDocumentUrl
     Taal = nl
     Status = Definitief
     URL naar het informatieobjecttype = https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/BesluitUuid
     ```

  * Service task: Opslaan document in documenten api - Rapportage
    Documenten API -> Document opslaan
     ```
     Bestandsnaam = leave empty
     Vertrouwelijkheidsaanduiding = zaakvertrouwelijk
     Titel = Rapportage  aanvraag ooievaarspas or any titel
     Beschrijving = Rapportage  aanvraag ooievaarspas or any beschrijving
     Naam procesvariabele met document = gegenereerdeStadspasDocumentUrl
     Naam procesvariabele voor opslag document-URL = gegenereerdeStadspasDocumentUrl
     Taal = nl
     Status = Definitief
     URL naar het informatieobjecttype = https://openzaak-zgw.test.denhaag.nl/catalogi/api/v1/informatieobjecttypen/RapportageUuid
     ```

  * Service task: Linken document aan de zaak
    Zaken API plugin action -> Koppel document aan zaak
     ```
     URL naar het document = pv:gegenereerdeStadspasDocumentUrl
     Documenttitel = Gegenereerde stadspas document or any title
     Documentbeschrijving = Gegenereerde stadspas document or any beschrijving
     ```
* Process completed portaaltask: configuration of the service tasks
  * Service task: Link document to zaak
    Zaken API plugin action -> Koppel document aan zaak
    ```
    URL naar het document = pv:documentUrl
    Documenttitel = Document uit het portaal or any title
    Documentbeschrijving = Document uit het portaal or any beschrijving
    ```

  * Service task: Update Portaal Taak Status
   Portaaltaak plugin action -> Portaaltaak afronden

* Stadspas: Opvragen informatie process: configuration of portal user task - Aanleveren informatie
  ```
  Formuliertype: URL
  Formulier URL: (formio object url from objecten API)
  Taakgegevens voor de ontvanger: 
    /informatieverzoeken = doc:/beoordelingEnAfhandeling/informatieverzoeken
  Ingevulde gegevens door de ontvanger: 
    doc:/beoordelingEnAfhandeling/informatieReacties/-/reactie = /reactie
  Ontvanger: Zaak-initiator (from drop down)
  ```
  note: Formulierdefinitie can also be used as Formuliertype if the form definition exists in the portal database.  
  




