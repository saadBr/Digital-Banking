import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http:HttpClient) { }

  changePassword(data: { oldPassword: string; newPassword: string }) {
  return this.http.post(`${environment.backendHost}/users/change-password`, data, {
    responseType: 'text'
  });
}

}
