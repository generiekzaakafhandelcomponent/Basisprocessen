import {DocumentDefinition, ProcessDocumentInstance} from '@valtimo/document';
import {ProcessInstanceTask} from '@valtimo/process';

export interface DossierLoader {
  documentDefinitionName: string;
  summaryForm?: object;
  userIdentityRoles: any;
  documentId: string;
  documentDefinition: DocumentDefinition;
  document: Document;
  tasks: Array<ProcessInstanceTask>;
  completedTasks: Array<HistoricalActivity>;
  processDocumentInstances: Array<ProcessDocumentInstance>;
  isEditAble: boolean;
}

export interface HistoricalActivity {
  activityName: string;
  activityType: string;
  startTime: string;
  endTime: string;
  durationInMillis: number;
  emdTimeUnix: number;
}
