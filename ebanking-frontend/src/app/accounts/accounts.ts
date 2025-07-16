import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AccountService } from '../services/account-service';
import { catchError, Observable, of } from 'rxjs';
import { AccountDetails } from '../model/account.model';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-accounts',
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  standalone: true,
  templateUrl: './accounts.html',
  styleUrl: './accounts.css'
})
export class Accounts implements OnInit {

  accountFormGroup!: FormGroup;
  operationFormGroup!: FormGroup;
  currentPage: number = 0;
  pageSize: number = 5;
  accountObservable!: Observable<AccountDetails | null>;
  statuses: string[] = ['ACTIVE', 'INACTIVE', 'BLOCKED', 'CLOSED', 'PENDING'];
  selectedStatus!: string;
  filterFormGroup!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    public authService: AuthService,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.filterFormGroup = this.fb.group({
      startDate: [''],
      endDate: [''],
      minAmount: [''],
      maxAmount: ['']
    });

    this.accountFormGroup = this.fb.group({
      accountId: this.fb.control("")
    });

    this.operationFormGroup = this.fb.group({
      operationType: this.fb.control(null),
      amount: this.fb.control(0),
      description: this.fb.control(null),
      accountDestination: this.fb.control(null)
    });

    const accountId = history.state?.accountId;
    if (accountId) {
      this.accountFormGroup.patchValue({ accountId });
      this.handleSearchAccount();
    } else {
      this.accountService.getLatestAccount().subscribe({
        next: latest => {
          this.accountFormGroup.patchValue({ accountId: (latest as any).id });
          this.handleSearchAccount();
        },
        error: err => {
          this.toast.showError(err?.error || "Failed to load latest account");
        }
      });
    }
  }

  handleSearchAccount() {
    const accountId = this.accountFormGroup.value.accountId;
    this.accountObservable = this.accountService.getAccount(accountId, this.currentPage, this.pageSize).pipe(
      catchError(err => {
        this.toast.showError(err?.error || "Account not found or backend error.");
        return of(null);
      })
    );

    this.accountObservable.subscribe(account => {
      if (account) {
        this.selectedStatus = account.status;
      }
    });
  }

  updateStatus() {
    const accountId = this.accountFormGroup.value.accountId;
    if (!accountId || !this.selectedStatus) return;

    this.accountService.updateStatus(accountId, this.selectedStatus).subscribe({
      next: () => {
        this.toast.showInfo("Status updated!");
        this.handleSearchAccount();
      },
      error: err => {
        this.toast.showError(err?.error || "Failed to update account status.");
      }
    });
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.handleSearchAccount();
  }

  handleAccountOperation() {
    const accountId = this.accountFormGroup.value.accountId;
    const { operationType, amount, description, accountDestination } = this.operationFormGroup.value;

    const successHandler = (msg: string) => {
      this.toast.showSuccess(msg);
      this.handleSearchAccount();
    };

    const errorHandler = (err: any) => {
      this.toast.showError(err?.error || "Operation failed");
    };

    switch (operationType) {
      case 'DEBIT':
        this.accountService.debit(accountId, amount, description).subscribe({ next: () => successHandler("Debited successfully"), error: errorHandler });
        break;
      case 'CREDIT':
        this.accountService.credit(accountId, amount, description).subscribe({ next: () => successHandler("Credited successfully"), error: errorHandler });
        break;
      case 'TRANSFER':
        this.accountService.transfer(accountId, accountDestination, amount, description).subscribe({ next: () => successHandler("Transfer successful"), error: errorHandler });
        break;
    }

    this.operationFormGroup.reset();
  }

  onCancelOperation(operationId: number): void {
    if (!confirm("Are you sure you want to cancel this operation?")) return;

    this.accountService.cancelOperation(operationId).subscribe({
      next: () => {
        this.toast.showInfo("Operation cancelled");
        this.handleSearchAccount();
      },
      error: err => {
        this.toast.showError(err?.error || "Failed to cancel operation.");
      }
    });
  }

  searchOperations() {
    const { startDate, endDate, minAmount, maxAmount } = this.filterFormGroup.value;
    const accountId = this.accountFormGroup.value.accountId;

    this.accountService.searchOperations(accountId, {
      startDate, endDate, minAmount, maxAmount, page: this.currentPage, size: this.pageSize
    }).subscribe({
      next: data => {
        this.accountObservable = of(data);
        this.currentPage = data.currentPage;
      },
      error: err => {
        this.toast.showError("Search failed. Please check inputs.");
      }
    });
  }
}
