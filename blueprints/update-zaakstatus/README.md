# Update Zaakstatus
## Configuration
- Add the files from the blueprint into the project root
- If you already have an AutoConfiguration file, merge the code into your existing file or create a separate one.
- Merge the dependencies in the build.gradle file into your existing file

## Prerequisites
- A working Valtimo instance
- A main process

This is a process that is used to update a case status. The next flows are supported:
- Update zaakstatus

## Miscellaneous requirements
The next factors are required for a completely working process
- OpenZaak. This blueprint makes use of the OpenZaak client. For more information please go to www.openzaak.org and check the [docs](https://docs.valtimo.nl/getting-started/modules/zgw/openzaak)

**Note:
This process is tested with Valtimo backend version: `10.5.0.RELEASE`**
