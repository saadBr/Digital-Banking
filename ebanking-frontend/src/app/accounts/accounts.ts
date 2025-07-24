import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { catchError, Observable, of } from 'rxjs';
import { AccountDetails } from '../model/account.model';
import { AccountService } from '../services/account-service';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-accounts',
  imports: [ReactiveFormsModule, CommonModule, FormsModule],
  standalone: true,
  templateUrl: './accounts.html',
  styleUrl: './accounts.css',
})
export class Accounts implements OnInit {
  newOverdraftLimit: number = 0;
  newInterestRate: number = 0;
  updateInterestRate() {
    const id = this.accountSnapshot.id;
    const rate = this.newInterestRate;

    this.accountService.updateInterestRate(id, rate!).subscribe({
      next: () => {
        this.toast.showSuccess('Interest rate updated!');
        this.handleSearchAccount();
      },
      error: (err) => {
        this.toast.showError(err?.error || 'Failed to update interest rate.');
      },
    });
  }

  updateOverdraftLimit() {
    const id = this.accountSnapshot.id;
    const limit = this.newOverdraftLimit;

    console.log('Updating overdraft limit:', id, limit);

    this.accountService.updateOverdraft(id, limit!).subscribe({
      next: () => {
        this.toast.showSuccess('Overdraft limit updated!');
        this.handleSearchAccount();
      },
      error: (err) => {
        this.toast.showError(err?.error || 'Failed to update overdraft limit.');
      },
    });
  }

  accountFormGroup!: FormGroup;
  operationFormGroup!: FormGroup;
  currentPage: number = 0;
  pageSize: number = 5;
  accountObservable!: Observable<AccountDetails | null>;
  statuses: string[] = ['ACTIVE', 'INACTIVE', 'BLOCKED', 'CLOSED', 'PENDING'];
  selectedStatus!: string;
  filterFormGroup!: FormGroup;
  accountSnapshot!: AccountDetails;
  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    public authService: AuthService,
    private toast: ToastService,
  ) {}
  isCurrent(account: AccountDetails): boolean {
    return account.type?.toLowerCase().includes('current');
  }

  isSaving(account: AccountDetails): boolean {
    return account.type?.toLowerCase().includes('saving');
  }
  ngOnInit(): void {
    this.filterFormGroup = this.fb.group({
      startDate: [''],
      endDate: [''],
      minAmount: [''],
      maxAmount: [''],
    });

    this.accountFormGroup = this.fb.group({
      id: this.fb.control(''),
    });

    this.operationFormGroup = this.fb.group({
      operationType: this.fb.control(null),
      amount: this.fb.control(0),
      description: this.fb.control(null),
      accountDestination: this.fb.control(null),
    });

    const formValueId = this.accountFormGroup?.value?.id;
    const routerId = history.state?.id;

    if (routerId) {
      this.accountFormGroup.patchValue({ id: routerId });
      this.handleSearchAccount();
    } else if (!formValueId) {
      this.accountService.getLatestAccount().subscribe({
        next: (latest) => {
          this.accountFormGroup.patchValue({ id: latest.id });
          this.handleSearchAccount();
        },
        error: (err) => {
          this.toast.showError(err?.error || 'Failed to load latest account');
        },
      });
    }
  }

  handleSearchAccount() {
    const id = this.accountFormGroup.value.id;
    this.accountObservable = this.accountService
      .getAccount(id, this.currentPage, this.pageSize)
      .pipe(
        catchError((err) => {
          this.toast.showError(
            err?.error || 'Account not found or backend error.',
          );
          return of(null);
        }),
      );

    this.accountObservable.subscribe((account) => {
      if (account) {
        this.accountSnapshot = account;
        this.selectedStatus = account.status;
        this.newOverdraftLimit = account.overdraftLimit ?? 0;
        this.newInterestRate = account.interestRate ?? 0;
      }
    });
  }

  updateStatus() {
    const id = this.accountFormGroup.value.id;
    if (!id || !this.selectedStatus) return;

    this.accountService.updateStatus(id, this.selectedStatus).subscribe({
      next: () => {
        this.toast.showInfo('Status updated!');
        this.handleSearchAccount();
      },
      error: (err) => {
        this.toast.showError(err?.error || 'Failed to update account status.');
      },
    });
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.handleSearchAccount();
  }

  handleAccountOperation() {
    const id = this.accountFormGroup.value.id;
    const { operationType, amount, description, accountDestination } =
      this.operationFormGroup.value;

    const successHandler = (msg: string) => {
      this.toast.showSuccess(msg);
      this.handleSearchAccount();
    };

    const errorHandler = (err: any) => {
      this.toast.showError(err?.error || 'Operation failed');
    };

    switch (operationType) {
      case 'DEBIT':
        this.accountService.debit(id, amount, description).subscribe({
          next: () => successHandler('Debited successfully'),
          error: errorHandler,
        });
        break;
      case 'CREDIT':
        this.accountService.credit(id, amount, description).subscribe({
          next: () => successHandler('Credited successfully'),
          error: errorHandler,
        });
        break;
      case 'TRANSFER':
        this.accountService
          .transfer(id, accountDestination, amount, description)
          .subscribe({
            next: () => successHandler('Transfer successful'),
            error: errorHandler,
          });
        break;
    }

    this.operationFormGroup.reset();
  }

  onCancelOperation(operationId: number): void {
    if (!confirm('Are you sure you want to cancel this operation?')) return;

    this.accountService.cancelOperation(operationId).subscribe({
      next: () => {
        this.toast.showInfo('Operation cancelled');
        this.handleSearchAccount();
      },
      error: (err) => {
        this.toast.showError(err?.error || 'Failed to cancel operation.');
      },
    });
  }

  searchOperations() {
    const { startDate, endDate, minAmount, maxAmount } =
      this.filterFormGroup.value;
    const id = this.accountFormGroup.value.id;

    this.accountService
      .searchOperations(id, {
        startDate,
        endDate,
        minAmount,
        maxAmount,
        page: this.currentPage,
        size: this.pageSize,
      })
      .subscribe({
        next: (data) => {
          this.accountObservable = of(data);
          this.currentPage = data.currentPage;
        },
        error: (err) => {
          this.toast.showError('Search failed. Please check inputs.');
        },
      });
  }
}
