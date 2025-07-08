import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { CommonModule } from '@angular/common';
import {Customer} from "../model/customer.model";

@Component({
  selector: 'app-customer-accounts',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './customer-accounts.html',
  styleUrl: './customer-accounts.css'
})
export class CustomerAccounts implements OnInit{
  customerId!:string;
  customer! : Customer;
  constructor(private route:ActivatedRoute, private router:Router) {
    this.customer=this.router.getCurrentNavigation()?.extras.state as Customer;
  }
    ngOnInit(): void {
        this.customerId=this.route.snapshot.params['id'];
    }

}
