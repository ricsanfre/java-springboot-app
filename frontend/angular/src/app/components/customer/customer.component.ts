import {Component, Input, OnInit} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerService} from "../../services/customer/customer.service";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {ConfirmationService, ConfirmEventType, MessageService} from "primeng/api";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit {
  display: boolean = false;
  customers: CustomerDTO[] = [];
  customer: CustomerRegistrationRequest = {};
  operation: 'create' | 'update' = 'create';

  constructor(
    private customerService: CustomerService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
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
    }
    if (this.operation === 'create') {
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
            if (err.status === 409) {
              this.messageService.add(
                {
                  severity: 'error',
                  summary: 'Customer not saved',
                  detail: err.error.message
                }
              )
            }
          }
        })
    } else if (this.operation === 'update') {
      this.customerService.updateCustomer(customer.id, customer)
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
            if (err.status === 409) {
              this.messageService.add(
                {
                  severity: 'error',
                  summary: 'Customer not saved',
                  detail: err.error.message
                }
              )
            }
          }
        })
    }
  }

  delete(customer: CustomerDTO) {
    if (customer) {
      this.confirmationService.confirm({
        message: `Are you sure you want to delete ${customer.name}?. You can't undo this action afterwards`,
        header: 'Delete Customer',
        //icon: 'pi pi-info-circle',
        accept: () => {
          // Go on and delete it
          this.customerService.deleteCustomer(customer.id)
            .subscribe({
              next: () => {
                // Refresh list of customers
                this.getAllCustomers();
                // Send success message
                this.messageService.add(
                  {
                    severity: 'success',
                    summary: 'Customer deleted',
                    detail: `Customer ${customer.name} successfully deleted`
                  })
              },
              error: (err) => {
                console.log(err);
              }
            })
        },
        reject: (type: ConfirmEventType) => {
          switch (type) {
            case ConfirmEventType.REJECT:
              this.messageService.add({severity: 'error', summary: 'Rejected', detail: 'You have rejected'});
              break;
            case ConfirmEventType.CANCEL:
              this.messageService.add({severity: 'warn', summary: 'Cancelled', detail: 'You have cancelled'});
              break;
          }
        }
      });

    }

  }

  update(customer: CustomerDTO) {
    this.operation = 'update';
    this.customer = customer;
    this.display = true;

  }

  create() {
    this.display = true;
    this.operation = 'create';
    this.customer = {};
  }

  cancel() {
    this.display = false;
    this.customer = {};
  }
}
