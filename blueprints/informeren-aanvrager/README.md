# Informeren aanvrager
## Prerequisites
- A working Valtimo instance

This is a process that is used to inform applicants. The next flows are supported:
- Creating an example case
- Control of said case
- Informeren aanvrager

## Miscellaneous requirements
The next factors are required for a completely working process
- The implementation of a mail module. In this blueprint the local-mail module is used. Please note that this doesn't send mail.

## Troubleshooting
If your cases tab is empty please do the following:
- In your environment menu go to ``Admin > Cases > Aanvraag`` and check if the process is connected to the document.
  - If not then click the `Connect process` button and select the `Example Main Process`.
- Link the forms to the process by:
  - going to the `Process links` tab and selecting the main process
  - click the start event and select the `example-form.start` as form definition.
  - now add the `example-controle-form` to the respective user task via the same method


**Note:
This process is tested with Valtimo backend version: `10.5.0.RELEASE`**
