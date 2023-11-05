import {Component, OnInit} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerService} from "../../services/customer/customer.service";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit {
  display: boolean = false;
  customers: CustomerDTO[] = [];
  customer: CustomerRegistrationRequest = {};

  constructor(
    private customerService: CustomerService,
    private messageService: MessageService
  ) {
  }

  ngOnInit(): void {
    this.getAllCustomers();

  }

  private getAllCustomers() {
    this.customerService.getAllCustomers()
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

  save(customer: CustomerRegistrationRequest) {
    if (customer) {
      console.log(customer);
      this.customerService.registerCustomer(customer)
        .subscribe({
          next: () => {
            console.log('Customer successfully saved');
            // Refresh list of customers
            this.getAllCustomers();
            // Hide sidebar
            this.display = false;
            // Reset customer request
            this.customer = {};
            // Send success message
            this.messageService.add(
              {
                severity: 'success',
                summary: 'Customer saved',
                detail: `Customer ${customer.name} successfully added`
              })
          },
          error: (err) => {
            console.log(err);
          }
        })
    }
  }
}
