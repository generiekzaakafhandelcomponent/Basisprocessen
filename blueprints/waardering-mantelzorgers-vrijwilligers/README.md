# Basisproces Waardering Mantelzorgers en Vrijwilligers

## Installatie
We gaan uit van een bestaande Valtimo GZAC backend repository. 

Kopiëer de bestanden van de `config` directory naar de config directory van Valtimo GZAC (`src/main/resources/config/`) 
en behoud dezelfde directory structuur.

Na het opstarten van Valtimo GZAC onder admin -> dossier -> WMV rollen toekennen aan het WMV dossier via "Rechten".
Na een refresh van de pagina is het WMV dossier beschikbaar onder Dossiers.

## Uitvoeren proces

Het proces kan gestart worden via het startformulier en bestaat voornamelijk uit automatische service taken. 
Alleen de service taken die gebruik maken van standaard Valtimo GZAC methodes zullen daadwerkelijk code uitvoeren.
Service taken die geconfigureerd kunnen worden met Plugin Actions of integreren met custom code zijn van commentaar voorzien
in het bpmn model..

Het proces bevat één message catch event. Deze message kan gesimuleerd worden met het ad-hoc "simuleren notificatie proces".
Deze kan gestart wordne via de knop "Start" op de dossier pagina.






