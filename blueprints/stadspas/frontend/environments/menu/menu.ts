import {ROLE_ADMIN, ROLE_DEVELOPER, ROLE_USER} from '@valtimo/config';
export const menuItems = [
  {roles: [ROLE_USER], link: ['/'], title: 'Dashboard', iconClass: 'icon mdi mdi-view-dashboard', sequence: 0},
  {roles: [ROLE_USER], title: 'Dossiers', iconClass: 'icon mdi mdi-layers', sequence: 1, children: []},
  {roles: [ROLE_USER], link: ['/tasks'], title: 'Tasks', iconClass: 'icon mdi mdi-check-all', sequence: 2},
  {roles: [ROLE_USER], link: ['/analysis'], title: 'Analysis', iconClass: 'icon mdi mdi-chart', sequence: 3},
  {
    roles: [ROLE_ADMIN], title: 'Admin', iconClass: 'icon mdi mdi-tune', sequence: 4, children: [
      {title: 'Basics', textClass: 'text-dark font-weight-bold c-default', sequence: 1},
      {link: ['/processes'], title: 'Processes', sequence: 2},
      {link: ['/form-management'], title: 'Forms', sequence: 3},
      {link: ['/decision-tables'], title: 'Decision tables', sequence: 4},
      {link: ['/dossier-management'], title: 'Dossiers', sequence: 5},
      {link: ['/object-management'], title: 'Objects', sequence: 6},
      {link: ['/connectors'], title: 'Connectors', sequence: 6},
      {link: ['/plugins'], title: 'Plugins', sequence: 7},
      {link: ['/form-links'], title: 'Form links', sequence: 7},
      {link: ['/process-links'], title: 'Form links Plugin', sequence: 9},
      {title: 'A&E', textClass: 'text-dark font-weight-bold c-default', sequence: 9},
      {link: ['/contexts'], title: 'Contexts', sequence: 10},
      {link: ['/users'], title: 'Users', sequence: 11},
      {link: ['/entitlements'], title: 'Entitlements', sequence: 12},
      {title: 'Other', textClass: 'text-dark font-weight-bold c-default', sequence: 13},
      {link: ['/process-migration'], title: 'Process migration', sequence: 14},
      {link: ['/choice-fields'], title: 'Choice fields', sequence: 15}
    ]
  },
  {
    roles: [ROLE_DEVELOPER], title: 'Development', iconClass: 'icon mdi mdi-code', sequence: 5, children: [
      {link: ['/swagger'], title: 'Swagger', iconClass: 'icon mdi mdi-dot-circle', sequence: 1}
    ]
  }
];
