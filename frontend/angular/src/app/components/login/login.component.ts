import { Component } from '@angular/core';
import {AuthenticationRequest} from "../../models/authentication-request";
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  authenticationRequest: AuthenticationRequest = {};
  errorMsg ='';

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {
  }

  login() {
    this.errorMsg = '';
    this.authenticationService.login(this.authenticationRequest)
      .subscribe({
        next: (authenticationResponse) => {
          console.log(authenticationResponse);
          // Store authentication response in locaStorage
          localStorage.setItem('user', JSON.stringify(authenticationResponse));
          // navigate to customers page
          this.router.navigate(['customers']);
        },
        error: (err) => {
          console.log(err);
          if (err.status === 401) {
            this.errorMsg = 'Login and/or password incorrect';
          }
        }
      })
  }

  register() {
    this.router.navigate(['register'])
  }
}
