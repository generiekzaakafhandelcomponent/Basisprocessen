export const customDefinitionTables = {
  'terugbelnotitie': [
    {
      propertyName: '$.klant.contactgegevens.telefoon',
      translationKey: 'terugbelnotitie.customerPhone',
      sortable: true
    },
    {
      propertyName: `$.klant.contactgegevens.naam`,
      translationKey: 'terugbelnotitie.customerName',
      sortable: true
    },
    {
      propertyName: `$.prio.hogePrioEmoji`,
      translationKey: 'terugbelnotitie.hogePrio',
      sortable: true
    },
    {
      propertyName: '$.caseMetadata.datumAangemaakt',
      translationKey: 'terugbelnotitie.dateCreated',
      viewType: 'date',
      sortable: true,
      default: true
    },
    {
      propertyName: 'modifiedOn',
      translationKey: 'lastModified',
      sortable: true,
      viewType: 'date'
    },
    {
      propertyName: '$.caseMetadata.datumUiterste',
      translationKey: 'terugbelnotitie.dateDeadline',
      viewType: 'date',
      sortable: true
    },
    {
      propertyName: '$.klantVraag.onderwerp',
      translationKey: 'terugbelnotitie.subject',
      sortable: true
    },
    {
      propertyName: '$.afhandeling.product',
      translationKey: 'terugbelnotitie.product',
      sortable: true
    },
    {
      propertyName: '$.interneStatus',
      translationKey: 'terugbelnotitie.statusIntern',
      sortable: true
    },
    {
      propertyName: 'assigneeFullName',
      translationKey: 'assigneeFullName',
      sortable: true
    },
    {
      propertyName: '$.afhandeling.afhandelaar.backOffice.naam',
      translationKey: 'terugbelnotitie.backOffice',
      sortable: true
    },
  ]
};
