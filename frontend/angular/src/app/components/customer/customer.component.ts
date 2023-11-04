import {Component, OnInit} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerService} from "../../services/customer/customer.service";
import {AuthenticationResponse} from "../../models/authentication-response";
import {JwtHelperService} from "@auth0/angular-jwt";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit {
  display: boolean = false;
  customers: CustomerDTO[] = [];

  constructor(
    private customerService: CustomerService
  ) {
  }

  ngOnInit(): void {
    this.getAllCustomers();

  }

  private getAllCustomers() {
    const token = this.getToken();
    console.log(token);
    if (token) {
      this.customerService.getAllCustomers(token)
        .subscribe({
          next: (response) => {
            console.log(response);
            this.customers = response;
          },
          error: (err) => {
            console.log(err);
          }
        })
    }
  }

  private getToken(): string | undefined {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      // Convert to JSON object
      const authResponse: AuthenticationResponse = JSON.parse(storedUser);
      const token = authResponse.token;
      if (token) {
        // Check if token is not expired
        const jwtHelper = new JwtHelperService();
        const isTokenNonExpired = !jwtHelper.isTokenExpired(token);
        if (isTokenNonExpired) {
          return token;
        }
      }
    }
    return undefined;
  }


}
