# Informeren aanvrager
[![en](https://img.shields.io/badge/lang-en-red.svg)](https://github.com/generiekzaakafhandelcomponent/Basisprocessen/blob/feature/generieke-zaak/README.md)

## Introduction

This blueprint provides a process to inform applicants. Two flows are provided:
- Informeren aanvrager (subprocess)
- Example main process (in order to use the subprocess)

## Prerequisites
- A working Valtimo instance
- A mail module. In this blueprint the local-mail module is used (which is a dummy implementation)

## Instructions
### Copy files
- Copy the directories and files in the `main/resource` directory of 'informeren-aanvrager' to your own Valtimo backend implementation below `src/main/resources`. 
### Restart Valtimo
- (re)start Valtimo your Valtimo instance
### Set Roles in the Valtimo interface
- Login (e.g. through http://localhost:4200) ->  `Admin > Cases > Aanvraag` and choose the roles that have access to this case.

## Additional information
- This example shows how to pass process variables from a call activity to a subprocesses. In this case 'emailTemplateSubject' and 'emailTemplateName'. This is configured in the`Inputs` section of the "Informeren aanvrager" call activity.

**Note:
This process is tested with Valtimo backend version: `10.5.0.RELEASE`**


