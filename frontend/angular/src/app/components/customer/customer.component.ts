import {Component, OnInit} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerService} from "../../services/customer/customer.service";

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
}
