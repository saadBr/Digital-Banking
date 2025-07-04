import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-customer-accounts',
  imports: [],
  templateUrl: './customer-accounts.html',
  styleUrl: './customer-accounts.css'
})
export class CustomerAccounts implements OnInit{
  customerId!:string;
  constructor(private route:ActivatedRoute) {
  }
    ngOnInit(): void {
        this.customerId=this.route.snapshot.params['id'];
    }

}
