/*
 * Copyright 2015-2020 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {ANALYZE_FOR_ENTRY_COMPONENTS, ModuleWithProviders, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DossierRoutingModule} from './dossier-routing.module';
import {DossierService} from './dossier.service';
import {TAB_MAP} from './dossier.config';
import {ConfigModule, HttpLoaderFactory} from '@valtimo/config';
import {
  BpmnJsDiagramModule,
  CamundaFormModule,
  DataListModule,
  DocumentenApiMetadataModalModule,
  DropzoneModule,
  FilterSidebarModule,
  FormIoModule,
  ListModule,
  ModalModule,
  SearchableDropdownSelectModule,
  SearchFieldsModule,
  SpinnerModule,
  TimelineModule,
  UploaderModule,
  WidgetModule,
} from '@valtimo/components';
import {ProcessModule} from '@valtimo/process';
import {NgbButtonsModule, NgbPaginationModule, NgbTooltipModule} from '@ng-bootstrap/ng-bootstrap';
import {FormModule} from '@valtimo/form';
import {FormsModule} from '@angular/forms';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {HttpClient} from '@angular/common/http';
import {TabService} from './tab.service';
import {TaskModule} from '@valtimo/task';
import {
  ButtonModule,
  InputLabelModule,
  ModalModule as VModalModule,
  ParagraphModule,
  SelectModule,
  TableModule,
  TitleModule,
} from '@valtimo/user-interface';
import {DossierListComponent} from './pages/dossier-list/dossier-list.component';
import {DossierDetailComponent} from './pages/dossier-detail/dossier-detail.component';
import {DossierDetailTabAuditComponent} from './pages/dossier-detail/tab/audit/audit.component';
import {DossierDetailTabProgressComponent} from './pages/dossier-detail/tab/progress/progress.component';
import {DossierDetailTabSummaryComponent} from './pages/dossier-detail/tab/summary/summary.component';
import {DossierDetailTabDocumentsComponent} from './pages/dossier-detail/tab/documents/documents.component';
import {DossierDetailTabContactMomentsComponent} from './pages/dossier-detail/tab/contact-moments/contact-moments.component';
import {DossierDetailTabZaakobjectenComponent} from './pages/dossier-detail/tab/zaakobjecten/zaakobjecten.component';
import {DossierUpdateComponent} from './components/dossier-update/dossier-update.component';
import {DossierProcessStartModalComponent} from './components/dossier-process-start-modal/dossier-process-start-modal.component';
import {DossierSupportingProcessStartModalComponent} from './components/dossier-supporting-process-start-modal/dossier-supporting-process-start-modal.component';
import {DossierDetailTabObjectTypeComponent} from './pages/dossier-detail/tab/object-type/object-type.component';
import {DossierDetailTabDocumentenApiDocumentsComponent} from './pages/dossier-detail/tab/documenten-api-documents/documenten-api-documents.component';
import {DossierDetailTabS3DocumentsComponent} from './pages/dossier-detail/tab/s3-documents/s3-documents.component';
import {DossierAssignUserComponent} from './components/dossier-assign-user/dossier-assign-user.component';

export type TabsFactory = () => Map<string, object>;

@NgModule({
  declarations: [
    DossierListComponent,
    DossierDetailComponent,
    DossierDetailTabSummaryComponent,
    DossierDetailTabProgressComponent,
    DossierDetailTabAuditComponent,
    DossierDetailTabDocumentsComponent,
    DossierDetailTabContactMomentsComponent,
    DossierDetailTabZaakobjectenComponent,
    DossierUpdateComponent,
    DossierProcessStartModalComponent,
    DossierSupportingProcessStartModalComponent,
    DossierDetailTabObjectTypeComponent,
    DossierDetailTabDocumentenApiDocumentsComponent,
    DossierDetailTabS3DocumentsComponent,
    DossierAssignUserComponent,
  ],
  imports: [
    CommonModule,
    DossierRoutingModule,
    ListModule,
    WidgetModule,
    BpmnJsDiagramModule,
    TimelineModule,
    CamundaFormModule,
    ProcessModule,
    FilterSidebarModule,
    NgbButtonsModule,
    DataListModule,
    FormsModule,
    FormModule,
    FormIoModule,
    ModalModule,
    SpinnerModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    TaskModule,
    ModalModule,
    NgbTooltipModule,
    UploaderModule,
    DropzoneModule,
    NgbPaginationModule,
    ConfigModule,
    SelectModule,
    InputLabelModule,
    ParagraphModule,
    TableModule,
    VModalModule,
    TitleModule,
    ButtonModule,
    DocumentenApiMetadataModalModule,
    SearchableDropdownSelectModule,
    SearchFieldsModule,
  ],
  exports: [DossierListComponent, DossierDetailComponent],
})
export class DossierModule {
  static forRoot(tabsFactory: TabsFactory): ModuleWithProviders<DossierModule> {
    return {
      ngModule: DossierModule,
      providers: [
        DossierService,
        TabService,
        {
          provide: TAB_MAP,
          useFactory: tabsFactory,
        },
        {
          provide: ANALYZE_FOR_ENTRY_COMPONENTS,
          useValue: Array.from(tabsFactory().values()),
          multi: true,
        },
      ],
    };
  }
}
