# Informeren aanvrager
[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/generiekzaakafhandelcomponent/Basisprocessen/blob/feature/generieke-zaak/README.md)
## Prerequisites
- A working Valtimo instance

This is a process that is used to inform applicants. The next flows are supported:
- Creating an example case
- Control of said case
- Informeren aanvrager

## Miscellaneous requirements
The next factors are required for a completely working process
- The implementation of a mail module. In this blueprint the local-mail module is used. Please note that this doesn't send mail. (the process will still work without this)
- Set the mail subject and template name under the `Inputs` section of the call activity.

## Troubleshooting
If your cases tab is empty please do the following:
- In your environment menu go to `Admin > Cases > Aanvraag` and check if the process is connected to the document and choose the roles that have access to this case.
**Note:
This process is tested with Valtimo backend version: `10.5.0.RELEASE`**
