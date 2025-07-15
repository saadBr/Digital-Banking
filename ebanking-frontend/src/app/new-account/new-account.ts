import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountService } from '../services/account-service';
import { CustomerService } from '../services/customer-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-new-account',
  templateUrl: './new-account.html',
  imports: [CommonModule,ReactiveFormsModule],
  styleUrls: ['./new-account.css'],
})
export class NewAccount implements OnInit {
  customerId!: any;
  form!: FormGroup;
  accountType = 'current'; // default
  customerName: string = '';

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private customerService: CustomerService
  ) {}

  ngOnInit(): void {
    this.customerId = +this.route.snapshot.params['id'];

    this.form = this.fb.group({
      initialBalance: [0],
      overdraft: [0],
      interestRate: [0],
      type: ['current']
    });

    this.customerService.getCustomer(this.customerId).subscribe({
      next: cust => this.customerName = cust.name,
      error: err => console.error(err)
    });
  }

  handleCreateAccount() {
    const formValue = this.form.value;
    const request: any = {
      customerId: this.customerId,
      type: formValue.type,
      initialBalance: formValue.initialBalance
    };

    if (formValue.type === 'current') {
      request.overdraft = formValue.overdraft;
    } else if (formValue.type === 'saving') {
      request.interestRate = formValue.interestRate;
    }

    this.accountService.createAccount(request).subscribe({
      next: () => {
        alert('Account created successfully!');
        this.router.navigate(['/admin/customers', this.customerId]);
      },
      error: err => console.error(err)
    });
  }
}
