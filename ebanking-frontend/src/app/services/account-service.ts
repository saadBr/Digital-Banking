import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { map, Observable } from 'rxjs';
import { AccountDetails } from '../model/account.model';
import { BankAccountRequestDTO } from '../model/BankAccountRequestDTO';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(private http: HttpClient) {}

  public getAccount(
    accountId: string,
    page: number,
    size: number,
  ): Observable<AccountDetails> {
    return this.http
      .get<AccountDetails>(
        environment.backendHost +
          '/accounts/' +
          accountId +
          '/pageOperations?page=' +
          page +
          '&size=' +
          size,
      )
      .pipe(
        map((res) => ({
          ...res,
          id: res.id ?? (res as any).accountId,
        })),
      );
  }
  public debit(accountId: string, amount: number, description: string) {
    let data = { accountId, amount, description };
    return this.http.post(environment.backendHost + '/accounts/debit', data);
  }
  public credit(accountId: string, amount: number, description: string) {
    let data = { accountId, amount, description };
    return this.http.post(environment.backendHost + '/accounts/credit', data);
  }
  public transfer(
    accountSource: string,
    accountDestination: string,
    amount: number,
    description: string,
  ) {
    let data = { accountSource, accountDestination, amount, description };
    return this.http.post(environment.backendHost + '/accounts/transfer', data);
  }
  public createAccount(account: BankAccountRequestDTO): Observable<any> {
    return this.http.post(environment.backendHost + '/accounts', account);
  }
  public updateStatus(accountId: string, newStatus: string): Observable<any> {
    return this.http.patch(
      `${environment.backendHost}/accounts/${accountId}/status`,
      { status: newStatus },
      { responseType: 'text' as 'json' },
    );
  }
  public cancelOperation(operationId: number): Observable<any> {
    return this.http.patch(
      `${environment.backendHost}/operations/${operationId}/cancel`,
      null,
      {
        responseType: 'text',
      },
    );
  }
  public searchOperations(
    accountId: string,
    params: any,
  ): Observable<AccountDetails> {
    let queryParams = new HttpParams();

    if (params.startDate) {
      queryParams = queryParams.set('startDate', params.startDate);
    }
    if (params.endDate) {
      queryParams = queryParams.set('endDate', params.endDate);
    }
    if (params.minAmount !== null && params.minAmount !== undefined) {
      queryParams = queryParams.set('minAmount', params.minAmount.toString());
    }
    if (params.maxAmount !== null && params.maxAmount !== undefined) {
      queryParams = queryParams.set('maxAmount', params.maxAmount.toString());
    }
    queryParams = queryParams.set('page', params.page.toString());
    queryParams = queryParams.set('size', params.size.toString());

    return this.http.get<AccountDetails>(
      `${environment.backendHost}/accounts/${accountId}/operations/search`,
      { params: queryParams },
    );
  }
  getLatestAccount(): Observable<AccountDetails> {
    return this.http.get<AccountDetails>(
      `${environment.backendHost}/accounts/latest`,
    );
  }

  updateInterestRate(id: string, newRate: number): Observable<void> {
    return this.http.put<void>(
      `${environment.backendHost}/accounts/${id}/interest-rate`,
      newRate,
    );
  }

  updateOverdraft(id: string, newLimit: number): Observable<void> {
    return this.http.put<void>(
      `${environment.backendHost}/accounts/${id}/overdraft`,
      newLimit,
    );
  }
}
