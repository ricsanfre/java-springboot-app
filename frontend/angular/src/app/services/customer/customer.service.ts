import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {CustomerDTO} from "../../models/customer-dto";

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(
    private http: HttpClient
  ) { }

  getAllCustomers(token: string): Observable<CustomerDTO[]> {
    return this.http.get<CustomerDTO[]>(
      'http://localhost:8080/api/v1/customer',
      {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      });
  }
}
