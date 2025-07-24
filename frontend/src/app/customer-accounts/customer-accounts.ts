import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Customer } from '../model/customer.model';
import { CustomerService } from '../services/customer-service';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-customer-accounts',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customer-accounts.html',
  styleUrl: './customer-accounts.css',
})
export class CustomerAccounts implements OnInit {
  customerId!: string;
  customer!: Customer;
  accounts: any[] = [];
  userForm!: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private customerService: CustomerService,
    private fb: FormBuilder,
    private router: Router,
    private toast: ToastService,
  ) {}

  navigateToNewAccount() {
    this.router.navigate([
      '/admin/customer-accounts',
      this.customerId,
      'new-account',
    ]);
  }

  ngOnInit(): void {
    this.customerId = this.route.snapshot.params['id'];

    this.userForm = this.fb.group({
      name: [''],
      email: [''],
    });

    this.loadCustomer();
    this.loadAccounts();
  }

  loadCustomer(): void {
    this.customerService.getCustomer(this.customerId).subscribe({
      next: (cust) => {
        this.customer = cust;
        this.userForm.patchValue({
          name: cust.name,
          email: cust.email,
        });
      },
      error: (err) => {
        console.error('Error loading customer:', err);
        this.toast.showError(err.error || 'Failed to load customer data');
      },
    });
  }

  loadAccounts(): void {
    this.customerService.getCustomerAccounts(this.customerId).subscribe({
      next: (accs) => (this.accounts = accs),
      error: (err) => {
        console.error('Error loading accounts:', err);
        this.toast.showError(err.error || 'Failed to load customer accounts');
      },
    });
  }

  handleUpdateUser(): void {
    const updatedCustomer: Customer = {
      id: this.customerId,
      name: this.userForm.value.name,
      email: this.userForm.value.email,
    };

    this.customerService.updateCustomer(updatedCustomer).subscribe({
      next: () => {
        this.toast.showInfo('Customer updated successfully.');
        this.loadCustomer();
      },
      error: (err) => {
        console.error('Update failed:', err);
        this.toast.showError(err.error || 'Failed to update customer.');
      },
    });
  }

  goToAccount(id: string) {
    this.router.navigate(['/admin/accounts'], {
      state: { id },
    });
  }
}
