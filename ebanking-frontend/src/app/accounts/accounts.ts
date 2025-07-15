import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AccountService} from '../services/account-service';
import {catchError, Observable, throwError, of} from 'rxjs';
import {AccountDetails} from '../model/account.model';
import {CommonModule} from '@angular/common';
import {AuthService} from '../services/auth.service';
import {Router} from '@angular/router';

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
  accountObservable!: Observable<AccountDetails>;
  errorMessage!: string;
  statuses: string[] = ['ACTIVE', 'INACTIVE', 'BLOCKED', 'CLOSED', 'PENDING'];
  selectedStatus!: string;
  filterFormGroup!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    public authService: AuthService,
    private router: Router
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
    }
  }

  handleSearchAccount() {
    let accountId: string = this.accountFormGroup.value.accountId;
    this.accountObservable = this.accountService.getAccount(accountId, this.currentPage, this.pageSize).pipe(
      catchError(err => {
        this.errorMessage = err.message;
        return throwError(err);
      })
    );
    this.accountObservable.subscribe(account => {
      this.selectedStatus = account.status;
    });
  }

  updateStatus() {
    let accountId: string = this.accountFormGroup.value.accountId;
    if (!accountId || !this.selectedStatus) return;
    this.accountService.updateStatus(accountId, this.selectedStatus).subscribe({
      next: () => {
        alert("Status updated!");
        this.handleSearchAccount();
      },
      error: err => {
        console.error(err);
      }
    });
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.handleSearchAccount();
  }

  handleAccountOperation() {
    let accountId: string = this.accountFormGroup.value.accountId;
    let operationType = this.operationFormGroup.value.operationType;
    let amount: number = this.operationFormGroup.value.amount;
    let description: string = this.operationFormGroup.value.description;
    let accountDestination: string = this.operationFormGroup.value.accountDestination;

    if (operationType === 'DEBIT') {
      this.accountService.debit(accountId, amount, description).subscribe({
        next: () => {
          alert("Success debit");
          this.handleSearchAccount();
        },
        error: err => console.log(err)
      });
    } else if (operationType === 'CREDIT') {
      this.accountService.credit(accountId, amount, description).subscribe({
        next: () => {
          alert("Success credit");
          this.handleSearchAccount();
        },
        error: err => console.log(err)
      });
    } else if (operationType === 'TRANSFER') {
      this.accountService.transfer(accountId, accountDestination, amount, description).subscribe({
        next: () => {
          alert("Success transfer");
          this.handleSearchAccount();
        },
        error: err => console.log(err)
      });
    }

    this.operationFormGroup.reset();
  }
  onCancelOperation(operationId: number): void {
    if (!confirm("Are you sure you want to cancel this operation?")) return;

    this.accountService.cancelOperation(operationId).subscribe({
      next: () => {
        this.handleSearchAccount();
      },
      error: err => {
        console.error("Cancel failed", err);
        alert("Error while cancelling operation: " + err.error);
      }
    });
  }

  searchOperations() {
  const { startDate, endDate, minAmount, maxAmount } = this.filterFormGroup.value;

  this.accountService.searchOperations(this.accountFormGroup.value.accountId, {
    startDate,
    endDate,
    minAmount,
    maxAmount,
    page: this.currentPage,
    size: 5
  }).subscribe({
    next: data => {
      this.accountObservable = of(data);
      this.currentPage = data.currentPage;
    },
    error: err => {
      console.error(err);
      this.errorMessage = 'Search failed. Please check inputs.';
    }
  });
}


}
