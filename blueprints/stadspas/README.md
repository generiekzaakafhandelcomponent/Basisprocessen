# Aanvraag  stadspas
**Stadspas (eng: city discount pass)**:
The 'Stadspas' is a city discount pass that the Dutch municipalities provide to the inhabitants that are below a certain income threshold. This pass will provide the holder all kinds of discounts to services provided by the city itself or other partners.

**These processes support:**

* The processing of an application for a 'Stadspas' by an inhabitant of the city.
* The processing of a request for information from the applicant by a case handler
* The processing of a formal decision on the application by the case handler
* The recording of the product 'Stadspas'
* Generation of the necessary emails and documents.

**Afhandelen Aanvraag**

* An applicant is able to complete a Open formulieren form on the website of the municipality.

* The data of the form is submitted as an object of the type 'Productaanvraag' in the Objects api. The Objects api also puts a notification on the Open notification message bus.

* GZAC has a subscription on these notifications and as a response, it will pull the 'productaanvraag' data from the Objects api and creates a case of the type 'Aanvraag Stadspas'.

* The first service task 'Vertalen ontvangen data naar document' of 'Stadspas: Afhandelen aanvraag' process converts and saves the received data to the document.

* A case handler can be assigned for each case just by clicking on Valtimo feature 'Gebruiker toewijzen aan dit dossier' (eng: 'Assign a user to this case').

* In the form-sequence (future: form flow) steps, the case handler controls and decides the information provided by the applicant. At the end, he can whether ask for additional information or make a decision (besluit).

* If additional information are asked, the applicant will be informed by email/post/portal. Whenever the requested information is received and communication preference is other than portal, the case handler can confirm it by starting an ad-hoc task 'Stadspas: Vastleggen reactie op informatieverzoek'. The case handler can again ask for more information or make a decision.

* If a decision is made, some documents will be generated. The case handler can record a besluit by selecting the uploaded besluit document, which will be then saved in open zaak. If the application is approved, 'Stadspas product' will be recorded in Objects api. The besluit will be sent to the applicant and the case will be closed.

* If a case handler takes langer than given periode (7 weeks) to handle the case,  an escalation flag (❗️❗️❗) will be displayed on the summary page of the case and on the case lists view, to alert the case handler.

**Connections with other systems**

OpenZaak, Objects-api, Objecttypes-api, Open-notifications, Smart documents, Wordpress mail.


**Prerequisites**

**Passwords and secrets**

As we don't want to commit secrets/passwords. A solution to work with secrets should already be there, either through the AWS parameter store or in Kubernetes with environment variables like below:

The following environment variables should be defined:

* VALTIMO_CONNECTOR-ENCRYPTION_SECRET= any secrets at least 16 characters
* VALTIMO_JWT_SECRET= keycloak jwt secret
* VALTIMO_PLUGIN_ENCRYPTION-SECRET= any secrets at least 16 characters
* CAMUNDA_BPM_ADMIN-USER_PASSWORD=any password
* KEYCLOAK_REALM= keyclaok realm
* KEYCLOAK_AUTH_SERVER_URL= keycloak auth url
* KEYCLOAK_RESOURCE= keycloak m2m client resource
* KEYCLOAK_CREDENTIALS_SECRET= keycloak credentials secret
* IMPLEMENTATION_STADSPAS_BESLUITTYPE=Besluit i.h.k.v. aanvraag stadspas

**Wordpress mail**

Make sure that the following templates are created and ready to use:
* ontvangstbevestiging-aanvraag-stadspas
* bewijsstukken-sturen
* besluit-aanvraag-stadspas

**Smart documents**

A user id (client id) and password are required to configure plug in and make sure that the following templates are available:
* toekenning-stadspas
* stadspas-besluitbrief-afwijzing
* stadspas-informatieverzoek

**Objecten api and Objecttypen api**

Make sure that the following objecttypes are created in objecttypen api and they are added in objecten api.

* stadspas-zaakdossiersync
* stadspas-product-stadspas
* stadspas-productaanvraag

**OpenZaak**

Make sure that zaaktype 'Aanvraag stadspas behandelen' is created in openZaak with the following properties:

**Status**
* Afgehandeld
* Vastleggen besluit
* Informatieverzoek uitgezet
* In behandeling genomen
* Aanvraag ontvangen

**Roltypen**
* Initiator
* Behandelaar

**Resultaattypen**
* Geannuleerd
* Niet geleverd
* Afgebroken
* Geleverd

**Besluittypen**
* Besluit i.h.k.v. aanvraag stadspas

**Informatieobjecttype**
* B&W Besluit
* Bijlage

**Open notification**

Make sure that a gzac application is added and client id and secret is available in order to connect the connector.

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
* OpenZaak
* SmartDocuments
* Zaken API

**Form links**

Form links with the user tasks are done in the backend. So it is not needed to do it in the UI.

**Process links**

Process links for openzaak service tasks is done via the backend code. So it is not needed to do it in the UI.

The following process links for plugins should be configured in Admin => Proceskoppelingen v2 (beta):
Select 'Genereer document stadspas' process and configure all the three service tasks ('Genereer document', 'Opslaan document in documenten api' and 'Linken document aan de zaak')

Note: please visit https://docs.valtimo.nl/ for further documentation. 
