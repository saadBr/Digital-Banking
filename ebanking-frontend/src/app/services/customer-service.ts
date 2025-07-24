import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../model/customer.model';
import { environment } from '../../environments/environment';
import { Customers } from '../customers/customers';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  constructor(private http: HttpClient) {}

  public getCustomers(): Observable<Array<Customer>> {
    return this.http.get<Array<Customer>>(
      environment.backendHost + '/customers',
    );
  }
  public searchCustomers(keyword: string): Observable<Array<Customer>> {
    return this.http.get<Array<Customer>>(
      environment.backendHost + '/customers/search?keyword=' + keyword,
    );
  }
  public saveCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(
      environment.backendHost + '/customers',
      customer,
    );
  }
  public deleteCustomer(id: string): Observable<Customer> {
    return this.http.delete<Customer>(
      environment.backendHost + '/customers/' + id,
    );
  }
  public getCustomer(id: string): Observable<Customer> {
    return this.http.get<Customer>(
      `${environment.backendHost}/customers/${id}`,
    );
  }

  public getCustomerAccounts(id: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${environment.backendHost}/customers/${id}/accounts`,
    );
  }

  public updateCustomer(customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(
      `${environment.backendHost}/customers/${customer.id}`,
      customer,
    );
  }
}
