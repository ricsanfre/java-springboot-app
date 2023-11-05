import { Component } from '@angular/core';
import { Router} from "@angular/router";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {CustomerService} from "../../services/customer/customer.service";
import {AuthenticationService} from "../../services/authentication/authentication.service";
import {AuthenticationRequest} from "../../models/authentication-request";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent {
  errorMsg = '';

  customer: CustomerRegistrationRequest = {};
  constructor(
    private router: Router,
    private customerService: CustomerService,
    private authenticationService: AuthenticationService
  ) {
  }

  login() {
    this.router.navigate(['login'])

  }

  register() {
    this.customerService.registerCustomer(this.customer)
      .subscribe({
        next: () => {
          // Perform login
          const authRequest: AuthenticationRequest = {
            username: this.customer.email,
            password: this.customer.password
          }
          this.authenticationService.login(authRequest)
            .subscribe({
              next: (authResponse) => {
                // Store authentication response in locaStorage
                localStorage.setItem('user', JSON.stringify(authResponse));
                // navigate to customers page
                this.router.navigate(['customers']);
              }
            });
        }
      });

  }
}
