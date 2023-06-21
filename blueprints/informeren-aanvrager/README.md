# Informeren aanvrager
[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/generiekzaakafhandelcomponent/Basisprocessen/blob/feature/generieke-zaak/README.md)
## Prerequisites
- A working Valtimo instance
- The implementation of a mail module. In this blueprint the local-mail module is used. Please note that because of that this implementation doesn't send an email. (the process will still work without this)

This is a process that is used to inform applicants. The next flows are provided:
- Creating an example case
- Informeren aanvrager

## Instructions
### Copy files
- Copy the folders and files from the blueprint process 'informeren-aanvrager'
### Restart Valtimo
- (re)start Valtimo
### Set Roles
- Select the roles needed to have access to the case. At first the cases tab is empty. To be able to select a case you need to set the roles. In order to do this go in your environment menu to `Admin > Cases > Aanvraag` and choose the roles that have access to this case.


## Additional information
- For the call activity to work, where the main process passes process variables to subprocesses, the mail subject and template name under the `Inputs` section of the call activity need to be set.
- In this example of the call activity 'Informeren aanvrager' under the tab `Input/Output`, the process variables 'emailTemplateSubject' and 'emailTemplateName' are passed to the send tasks of the subprocess.



**Note:
This process is tested with Valtimo backend version: `10.5.0.RELEASE`**
