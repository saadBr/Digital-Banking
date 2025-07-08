import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar} from './navbar/navbar';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Customers} from './customers/customers';
import {Accounts} from './accounts/accounts';
import {Login} from './login/login';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,Navbar,FormsModule,Customers,Accounts,ReactiveFormsModule,Login],
  standalone: true,
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'ebanking-frontend';
}
