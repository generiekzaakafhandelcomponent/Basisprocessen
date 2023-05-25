# Terugbelnotitie
## Processen
Dit is een proces om terugbelnotities aan te verwerken. De volgende flows worden ondersteund:
- Aanmaken terugbelnotitie
- Afhandelen terugbelnotitie
- Verwerken terugbelnotitie
- Doorvoeren wijziging terugbelnotitie
- Afsluiten terugbelnotitiezaak

## Connectors
De volgende Connectors zijn nodig voor een volledige werking van de Terugbelnotitie processen:
- OpenZaak
- Objecten API (Zaakdetails)
- Besluiten

## Overige eisen
Voor een volledige werking moet aan de volgende eisen voldaan worden:
- Een implementatie van een mail module. In dit basisproces wordt de local-mail module gebruikt. Deze stuurt echter geen mails.

## Proceskoppelingen
In de volgende processen moet een proceskoppeling worden gemaakt:
- Verwerken terugbelnotitie
    - Taak 'Zet zaakstatus in behandeling'
- Afsluiten terugbelnotitiezaak
    - Taak 'Zet zaakresultaat afgesloten'
    - Taak 'Zet status en zaakstatus afgehandeld'

**Note:
This process is tested with Valtimo backend version: 9.25.0.RELEASE and frontend version: 5.14.0**