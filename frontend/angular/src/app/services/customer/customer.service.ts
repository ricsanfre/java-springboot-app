import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {CustomerDTO} from "../../models/customer-dto";
import {environment} from "../../../environments/environment";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private readonly customerUrl = `${environment.api.baseUrl}${environment.api.customersUri}`
  constructor(
    private http: HttpClient
  ) { }

  getAllCustomers(): Observable<CustomerDTO[]> {
    return this.http.get<CustomerDTO[]>(
      this.customerUrl);
  }

  registerCustomer(customerRegistrationRequest: CustomerRegistrationRequest): Observable<void> {
    return this.http.post<void>(this.customerUrl, customerRegistrationRequest);
  }

  deleteCustomer(customerId: number | undefined): Observable<void> {
    return this.http.delete<void>(`${this.customerUrl}/${customerId}`);
  }
}
