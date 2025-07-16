import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../services/customer-service';
import { catchError, map, Observable, throwError, of } from 'rxjs';
import { Customer } from '../model/customer.model';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-customers',
  imports: [CommonModule, ReactiveFormsModule],
  standalone: true,
  templateUrl: './customers.html',
  styleUrl: './customers.css'
})
export class Customers implements OnInit {
  customers!: Observable<Array<Customer>>;
  errorMessage!: string;
  searchFormGroup!: FormGroup;

  constructor(
    private customerService: CustomerService,
    private fb: FormBuilder,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.searchFormGroup = this.fb.group({
      keyword: this.fb.control("")
    });
    this.handleSearchCustomers();
  }

  handleSearchCustomers() {
    const kw = this.searchFormGroup.value.keyword;
    this.customers = this.customerService.searchCustomers(kw).pipe(
      catchError(err => {
        this.toast.showError(err?.error || "Failed to fetch customers");
        return of([]); // fallback to empty list
      })
    );
  }

  handleDeleteCustomer(c: Customer) {
    if (!confirm("Are you sure you want to delete this customer?")) return;

    this.customerService.deleteCustomer(c.id).subscribe({
      next: () => {
        this.toast.showInfo("Customer deleted successfully");
        this.customers = this.customers.pipe(
          map(data => {
            const updated = [...data];
            const index = updated.findIndex(item => item.id === c.id);
            if (index !== -1) updated.splice(index, 1);
            return updated;
          })
        );
      },
      error: err => {
        console.error(err);
        this.toast.showError(err?.error || "Failed to delete customer");
      }
    });
  }

  handleCustomerAccount(c: Customer) {
    this.router.navigateByUrl("/admin/customer-accounts/" + c.id, { state: c });
  }
}
