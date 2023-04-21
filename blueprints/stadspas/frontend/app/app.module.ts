import {BrowserModule} from '@angular/platform-browser';
import {Injector, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpBackend, HttpClientModule} from '@angular/common/http';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {LayoutModule} from '@valtimo/layout';
import {TaskModule} from '@valtimo/task';
import {environment} from '../environments/environment';
import {SecurityModule} from '@valtimo/security';
import {
    BpmnJsDiagramModule,
    CardModule,
    MenuModule,
    registerFormioUploadComponent,
    WidgetModule,
    FormIoModule,
    registerFormioFileSelectorComponent,
    registerDocumentenApiFormioUploadComponent
} from '@valtimo/components';
import {ChoicefieldModule} from '@valtimo/choicefield';
import {
    DefaultTabs,
    DossierDetailTabAuditComponent,
    DossierDetailTabDocumentsComponent,
    DossierDetailTabNotesComponent,
    DossierDetailTabProgressComponent,
    DossierDetailTabSummaryComponent,
    DossierModule,
} from '@valtimo/dossier';
import {ProcessModule} from '@valtimo/process';
import {ViewConfiguratorModule} from '@valtimo/view-configurator';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ContextModule} from '@valtimo/context';
import {DashboardModule} from '@valtimo/dashboard';
import {DocumentModule} from '@valtimo/document';
import {AccountModule} from '@valtimo/account';
import {UserManagementModule} from '@valtimo/user-management';
import {AuthorityModule} from '@valtimo/authority';
import {ChoiceFieldModule} from '@valtimo/choice-field';
import {ResourceModule} from '@valtimo/resource';
import {FormModule} from '@valtimo/form';
import {SwaggerModule} from '@valtimo/swagger';
import {AnalyseModule} from '@valtimo/analyse';
import {ProcessManagementModule} from '@valtimo/process-management';
import {DecisionModule} from '@valtimo/decision';
import {MilestoneModule} from '@valtimo/milestone';
import {LoggerModule} from 'ngx-logger';
import {FormManagementModule} from '@valtimo/form-management';
import {ManagementContextModule} from '@valtimo/management';
import {FormLinkModule} from '@valtimo/form-link';
import {MigrationModule} from '@valtimo/migration';
import {DossierManagementModule} from '@valtimo/dossier-management';
import {BootstrapModule} from '@valtimo/bootstrap';
import {ConfigModule, ConfigService, MultiTranslateHttpLoaderFactory} from '@valtimo/config';
import {OpenZaakModule} from '@valtimo/open-zaak';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {DossierLoaderComponent} from '../custom-components/dossier-loader/dossier-loader.component';
import {ConnectorManagementModule} from '@valtimo/connector-management';
import {
    PLUGINS_TOKEN,
    OpenZaakPluginModule,
    openZaakPluginSpecification,
    SmartDocumentsPluginModule,
    smartDocumentsPluginSpecification,
    ZakenApiPluginModule,
    zakenApiPluginSpecification,
    DocumentenApiPluginModule,
    documentenApiPluginSpecification,
    CatalogiApiPluginModule,
    catalogiApiPluginSpecification,
} from '@valtimo/plugin';
import {PluginManagementModule} from '@valtimo/plugin-management';

export function tabsFactory() {
    return new Map<string, object>([
        [DefaultTabs.summary, DossierDetailTabSummaryComponent],
        [DefaultTabs.progress, DossierDetailTabProgressComponent],
        [DefaultTabs.audit, DossierDetailTabAuditComponent],
        [DefaultTabs.documents, DossierDetailTabDocumentsComponent],
        [DefaultTabs.notes, DossierDetailTabNotesComponent],
    ]);
}

@NgModule({
    declarations: [
        AppComponent,
        DossierLoaderComponent
    ],
    imports: [
        HttpClientModule,
        CommonModule,
        BrowserModule,
        AppRoutingModule,
        LayoutModule,
        CardModule,
        WidgetModule,
        BootstrapModule,
        ConfigModule.forRoot(environment),
        LoggerModule.forRoot(environment.logger),
        environment.authentication.module,
        SecurityModule,
        MenuModule,
        TaskModule,
        ChoicefieldModule,
        DossierModule.forRoot(tabsFactory),
        ProcessModule,
        ViewConfiguratorModule,
        BpmnJsDiagramModule,
        FormsModule,
        ReactiveFormsModule,
        ContextModule,
        DashboardModule,
        DocumentModule,
        AccountModule,
        UserManagementModule,
        AuthorityModule,
        ChoiceFieldModule,
        ResourceModule,
        FormModule,
        ManagementContextModule,
        AnalyseModule,
        SwaggerModule,
        ConnectorManagementModule,
        ProcessManagementModule,
        DecisionModule,
        MilestoneModule,
        FormManagementModule,
        FormLinkModule,
        MigrationModule,
        DossierManagementModule,
        OpenZaakModule,
        OpenZaakPluginModule,
        PluginManagementModule,
        SmartDocumentsPluginModule,
        ZakenApiPluginModule,
        DocumentenApiPluginModule,
        CatalogiApiPluginModule,
        HttpClientModule, TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: MultiTranslateHttpLoaderFactory,
                deps: [HttpBackend, ConfigService]
            }
        }), FormIoModule
    ],
    providers: [
        {
            provide: PLUGINS_TOKEN,
            useValue: [
                smartDocumentsPluginSpecification,
                documentenApiPluginSpecification,
                zakenApiPluginSpecification,
                openZaakPluginSpecification,
                catalogiApiPluginSpecification,
            ],
        },
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
    constructor(injector: Injector) {
        registerFormioUploadComponent(injector);
        registerFormioFileSelectorComponent(injector);
        registerDocumentenApiFormioUploadComponent(injector);
    }
}
