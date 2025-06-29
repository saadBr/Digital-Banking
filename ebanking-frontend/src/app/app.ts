import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar} from './navbar/navbar';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Customers} from './customers/customers';
import {Accounts} from './accounts/accounts';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,Navbar,FormsModule,Customers,Accounts,ReactiveFormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'ebanking-frontend';
}
