export const customDefinitionColumns = {
    'aanvraag-stadspas': [
        {
            propertyName: '$.aanvrager.volledigeNaam',
            translationKey: 'sd.naamAanvrager',
            sortable: true
        },
        {
            propertyName: '$.aanvrager.telefoonnummer',
            translationKey: 'sd.telefoonnummerAanvrager',
            sortable: true
        },
        {
            propertyName: '$.aanvrager.emailadres',
            translationKey: 'sd.emailadresAanvrager',
            sortable: true
        },
        {
            propertyName: '$.datumAanvraagOntvangen',
            translationKey: 'sd.datumAanvraagOntvangen',
            sortable: true
        },
        {
            propertyName: '$.escalatie.isGeescaleerd',
            translationKey: 'sd.isGeescaleerd',
            sortable: true,
            viewType: 'boolean',
            enum: ['❗️❗️❗', '-']
        },
        {
            propertyName: '$.openzaak.statusOmschrijving',
            translationKey: 'sd.statusAanvrager',
            sortable: true
        },
        {
            propertyName: '$.openzaak.resulaatOmschrijving',
            translationKey: 'sd.resultaatAanvrager',
            sortable: true
        },
        {
            propertyName: 'assigneeFullName',
            translationKey: 'assigneeFullName',
            sortable: true
        },
        {
            propertyName: 'modifiedOn',
            translationKey: 'lastModified',
            sortable: true,
            viewType: 'date',
            default: true
        },
        {
            propertyName: 'relatedFiles.length',
            translationKey: 'sd.aantalBijlagen',
            sortable: false
        }
    ]
};
