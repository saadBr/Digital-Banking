import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  changePassword(data: { oldPassword: string; newPassword: string }) {
    return this.http.post(
      `${environment.backendHost}/users/change-password`,
      data,
      {
        responseType: 'text',
      },
    );
  }

  createUser(user: any) {
    return this.http.post(`${environment.backendHost}/users/create-user`, user);
  }

  getAllUsers() {
    return this.http.get<User[]>(`${environment.backendHost}/users`);
  }

  deleteUser(userId: string) {
    return this.http.delete(`${environment.backendHost}/users/${userId}`, {
      responseType: 'text' as 'json',
    });
  }

  updateUser(userId: string, data: any) {
    return this.http.put(`${environment.backendHost}/users/${userId}`, data);
  }

  resetPassword(username: string, newPassword: string) {
    return this.http.post(
      `${environment.backendHost}/users/reset-password`,
      {
        username,
        newPassword,
      },
      { responseType: 'text' as 'json' },
    );
  }
}
