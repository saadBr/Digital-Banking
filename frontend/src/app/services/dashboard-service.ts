import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { DashboardStats } from '../model/dashboard.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  constructor(private http: HttpClient) {}

  getAccountsStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(
      environment.backendHost + '/dashboard',
    );
  }

  getOperationsByType() {
    return this.http.get(
      environment.backendHost + '/dashboard/operationsByType',
    );
  }

  getMostActiveCustomers() {
    return this.http.get<{ [key: string]: number }>(
      environment.backendHost + '/dashboard/most-active-customers',
    );
  }
}
