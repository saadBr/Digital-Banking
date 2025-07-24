import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Customer } from '../model/customer.model';
import { CustomerService } from '../services/customer-service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-new-customer',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './new-customer.html',
  styleUrl: './new-customer.css',
})
export class NewCustomer implements OnInit {
  newCustomerFormGroup!: FormGroup;
  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private router: Router,
    private toast: ToastService,
  ) {}
  ngOnInit() {
    this.newCustomerFormGroup = this.fb.group({
      name: this.fb.control(null, [
        Validators.required,
        Validators.minLength(4),
      ]),
      email: this.fb.control(null, [Validators.required, Validators.email]),
    });
  }

  handleSaveCustomer() {
    let customer: Customer = this.newCustomerFormGroup.value;
    this.customerService.saveCustomer(customer).subscribe({
      next: (data) => {
        this.toast.showSuccess('Customer saved successfully!');
        this.router.navigateByUrl('/admin/customers');
      },
      error: (err) => {
        this.toast.showError(err?.error || 'Failed to create customer');
      },
    });
  }
}
