import {Component, OnInit} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {KeycloakService} from 'keycloak-angular';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  constructor(private router: Router, private keycloakService: KeycloakService) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        localStorage.removeItem('list-search-terugbelnotitie');
      }
    });
  }

  ngOnInit() {
    this.getUserIdentity();
  }

  getUserIdentity() {
    this.keycloakService.loadUserProfile().then((profile) => {
      localStorage.setItem('userIdentity', JSON.stringify(profile));
    });
  }
}
