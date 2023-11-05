import {Component, Input} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";

@Component({
  selector: 'app-customer-card',
  templateUrl: './customer-card.component.html',
  styleUrls: ['./customer-card.component.scss']
})
export class CustomerCardComponent {
  @Input()
  customer: CustomerDTO = {};

  get customerImage(): string {
    const manOrWoman = this.customer.gender === 'MALE'? 'men': 'women';
    return `https://randomuser.me/api/portraits/${manOrWoman}/${this.customer.id}.jpg`
  }
}
