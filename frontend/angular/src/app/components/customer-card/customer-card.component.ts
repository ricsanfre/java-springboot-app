import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";

@Component({
  selector: 'app-customer-card',
  templateUrl: './customer-card.component.html',
  styleUrls: ['./customer-card.component.scss']
})
export class CustomerCardComponent {
  @Input()
  customer: CustomerDTO = {};

  @Output()
  delete: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();


  get customerImage(): string {
    const manOrWoman = this.customer.gender === 'MALE'? 'men': 'women';
    return `https://randomuser.me/api/portraits/${manOrWoman}/${this.customer.id}.jpg`
  }

  onDelete() {
    this.delete.emit(this.customer)
  }
}
