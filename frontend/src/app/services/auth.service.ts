import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  isAuthenticated: boolean = false;
  roles: any;
  username: any;
  accessToken!: any;
  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  public login(username: string, password: string) {
    let options = {
      headers: new HttpHeaders().set(
        'Content-Type',
        'application/x-www-form-urlencoded',
      ),
    };
    let params = new HttpParams()
      .set('username', username)
      .set('password', password);
    return this.http.post(
      environment.backendHost + '/auth/login',
      params,
      options,
    );
  }
  loadProfile(data: any) {
    this.isAuthenticated = true;
    this.accessToken = data['access_token'];
    let jwtDecoded: any = jwtDecode(this.accessToken);
    this.username = jwtDecoded.sub;
    this.roles = jwtDecoded.scope;
    window.localStorage.setItem('jwt-token', this.accessToken);
  }

  logout() {
    this.isAuthenticated = false;
    this.accessToken = undefined;
    this.username = undefined;
    this.roles = undefined;
    window.localStorage.removeItem('jwt-token');
    this.router.navigateByUrl('/login');
  }

  loadJwtTokenFromLocalStorage() {
    let token = window.localStorage.getItem('jwt-token');
    if (token) {
      this.loadProfile({ access_token: token });
      this.router.navigateByUrl('/admin/customers');
    }
  }
}
