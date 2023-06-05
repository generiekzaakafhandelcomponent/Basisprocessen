# Informeren aanvrager
## Prerequisites
- A working Valtimo instance

In this blueprint you will find an example process that you can use to get a better understanding of how the informeren aanvrager process is implemented.

## Configuration
- Add the files from the blueprint into the project root
- Add user roles to main process
## Troubleshooting
If your cases tab is empty please do the following:
- In your environment menu go to ``Admin > Cases > Aanvraag`` and check if the process is connected to the document. 
  - If not then click the `Connect process` button and select the `Example Main Process`.
- Link the forms to the process by:
  - going to the `Process links` tab and selecting the main process
  - click the start event and select the `example-form.start` as form definition.
  - now add the `example-controle-form` to the respective user task via the same method


This blueprint was tested on Valtimo `10.5.0.RELEASE`.

