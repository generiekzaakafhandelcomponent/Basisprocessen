import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DossierLoader} from './dossier-loader.model';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {DocumentService} from '@valtimo/document';
import {TaskService} from '@valtimo/task';
import {ProcessService} from '@valtimo/process';
import * as moment_ from 'moment';
import {noop} from 'rxjs';
import {DossierLoaderService} from './dossier-loader.service';
import {FormService} from '@valtimo/form';
import {UserProviderService} from '@valtimo/security';

const moment = moment_;
moment.locale(localStorage.getItem('langKey') || '');

@Component({
  selector: 'app-dossier-loader',
  templateUrl: './dossier-loader.component.html',
  styles: []
})
export class DossierLoaderComponent implements OnInit {
  @Output() dossierLoaded?: EventEmitter<DossierLoader> = new EventEmitter();
  @Input() documentDefinitionName?: string;
  @Input() documentId?: string;
  @Input() activeTabSummaryFormName?: string;

  public dossier: DossierLoader = {} as DossierLoader;
  private snapshot: ParamMap;
  public activeTab: string;
  public taskIdentityLinksGroupIds: any = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private documentService: DocumentService,
    private taskService: TaskService,
    private processService: ProcessService,
    private dossierLoaderService: DossierLoaderService,
    private userProviderService: UserProviderService,
    private formService: FormService
  ) {
    this.snapshot = this.route.snapshot.paramMap;
    this.userProviderService.getUserSubject().subscribe(userIdentity => {
      this.dossier.userIdentityRoles = userIdentity.roles;
    });
  }

  ngOnInit(): void {
    this.dossier.tasks = [];
    this.dossier.completedTasks = [];
    this.dossier.documentDefinitionName = this.documentDefinitionName ? this.documentDefinitionName : this.snapshot.get('documentDefinitionName');
    this.dossier.documentId = this.documentId ? this.documentId : this.snapshot.get('documentId');
    if (this.dossier.documentDefinitionName && this.dossier.documentId) {
      this.loadProcessDocumentInstances(this.dossier.documentId).then( () => {
        this.documentService.getDocumentDefinition(this.dossier.documentDefinitionName).subscribe(documentDefinition => {
          this.dossier.documentDefinition = documentDefinition;
        }, err => noop());

        this.documentService.getDocument(this.dossier.documentId).subscribe(document => {
          // @ts-ignore
          this.dossier.document = document;
          if (this.activeTabSummaryFormName) {
            this.formService.getFormDefinitionByNamePreFilled(this.activeTabSummaryFormName, this.dossier.documentId).subscribe(form => {
              this.dossier.summaryForm = {
                name: this.activeTabSummaryFormName,
                definition: form
              };
            }, err => noop());
          }
        }, err => noop());
      });
    }
  }

  private async loadProcessDocumentInstances(documentId: string) {
    this.documentService.findProcessDocumentInstances(documentId).subscribe(processDocumentInstances => {
      this.dossier.processDocumentInstances = processDocumentInstances;
      this.dossier.processDocumentInstances.map(async instance => {
        await this.loadProcessInstanceTasks(instance.id.processInstanceId);
      });
    }, err => noop());
  }

  private async loadProcessInstanceTasks(processInstanceId: string) {
    // await this.loadCompletedProcessInstanceTasks(processInstanceId);
    this.processService.getProcessInstanceTasks(processInstanceId).subscribe(async tasks => {
      if (tasks) {
        tasks.map(task => {
          this.taskIdentityLinksGroupIds = this.taskIdentityLinksGroupIds.concat([...new Set(task.identityLinks.map(item => item.groupId))]);
          task.createdUnix = moment(task.created).unix();
          task.created = moment(task.created).format('DD MMM YYYY HH:mm');
          task.isLocked = () => {
            let locked = true;
            for (const link of task.identityLinks) {
              if (link.type === 'candidate' && link.groupId) {
                if (this.dossier.userIdentityRoles.includes(link.groupId)) {
                  locked = false;
                  break;
                }
              }
            }
            return locked;
          };
        });
        this.dossier.tasks = this.dossier.tasks.concat(tasks);
        this.dossier.tasks.sort((t1, t2) => t2.createdUnix - t1.createdUnix);
        // edit able when ROLE_COACH task and user with role ROLE_COACH
        this.dossier.isEditAble = (this.taskIdentityLinksGroupIds.includes('ROLE_COACH') && this.dossier.userIdentityRoles.includes('ROLE_COACH')) || false;
        this.onDossierLoaded();
      } else {
        this.dossier.tasks = [];
        this.onDossierLoaded();
      }
    }, err => noop());
  }

  private async loadCompletedProcessInstanceTasks(processInstanceId: string) {
    this.dossierLoaderService.getProcessInstanceHistory(processInstanceId).subscribe(activities => {
      const completedTasks = activities.filter(activity => activity['activityType'] === 'userTask' && activity['endTime'] !== null);
      completedTasks.forEach(task => {
        task.emdTimeUnix = moment(task.endTime).unix();
        task.endTime = moment(task.endTime).format('DD MMMM YYYY HH:mm');
        task.startTime = moment(task.startTime).format('DD MMMM YYYY HH:mm');
      });
      this.dossier.completedTasks = this.dossier.completedTasks.concat(completedTasks);
      this.dossier.completedTasks.sort((t1, t2) => t2.emdTimeUnix - t1.emdTimeUnix);

    }, err => noop());
  }

  public onDossierLoaded() {
    this.dossierLoaded.emit(this.dossier);
  }

}
