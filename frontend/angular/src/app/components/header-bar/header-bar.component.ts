import { Component } from '@angular/core';
import {MenuItem, MenuItemCommandEvent} from "primeng/api";
import {AuthenticationResponse} from "../../models/authentication-response";
import {JwtHelperService} from "@auth0/angular-jwt";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header-bar',
  templateUrl: './header-bar.component.html',
  styleUrls: ['./header-bar.component.scss']
})
export class HeaderBarComponent {

  constructor(
    private router: Router
  ) {
  }
  items: MenuItem[] = [
    {
      label:'Profile',
      icon: 'pi pi-user'
    },
    {
      label:'Settings',
      icon: 'pi pi-cog'
    },
    {
      separator: true
    },
    {
      label:'Sign out',
      icon: 'pi pi-sign-out',
      command: () => {
        console.log('Sign out');
        // Clean local storage
        localStorage.removeItem('user');
        // navigate
        this.router.navigate(['login']);
      }
    }
  ];

  get userName():string  {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      // Convert to JSON object
      const authResponse: AuthenticationResponse = JSON.parse(storedUser);
      const userName = authResponse.customerDTO?.userName
      if (userName) {
        return userName;
      }
    }
    return '--';
  }

  get userRole():string {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      // Convert to JSON object
      const authResponse: AuthenticationResponse = JSON.parse(storedUser);
      const userRole = authResponse.customerDTO?.userRoles;
      if (userRole) {
        return userRole[0];
      }
    }
    return '--';
  }

}
