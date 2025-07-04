import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {AccountService} from '../services/account-service';
import {catchError, Observable, throwError} from 'rxjs';
import {AccountDetails} from '../model/account.model';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-accounts',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css'
})
export class Accounts implements OnInit{
  accountFormGroup!: FormGroup;
  operationFormGroup!: FormGroup;
  currentPage:number = 0;
  pageSize:number = 5;
  accountObservable!:Observable<AccountDetails>;
  errorMessage!:string;
  constructor(private fb:FormBuilder, private accountService:AccountService) {
  }

  ngOnInit(): void {
      this.accountFormGroup=this.fb.group({
        accountId: this.fb.control("")
      });
      this.operationFormGroup=this.fb.group({
        operationType:this.fb.control(null),
        amount:this.fb.control(0),
        description:this.fb.control(null),
        accountDestination:this.fb.control(null)
      });
    }


  handleSearchAccount() {
    let accountId:string = this.accountFormGroup.value.accountId;
    this.accountObservable=this.accountService.getAccount(accountId, this.currentPage, this.pageSize).pipe(
      catchError(err => {
        this.errorMessage=err.message;
        return throwError(err);
      })
    );
  }

  goToPage(page: number) {
    this.currentPage=page;
    this.handleSearchAccount();
  }

  handleAccountOperation() {
    let accountId: string = this.accountFormGroup.value.accountId;
    let operationType = this.operationFormGroup.value.operationType;
    let amount:number = this.operationFormGroup.value.amount;
    let description:string = this.operationFormGroup.value.description;
    let accountDestination:string=this.operationFormGroup.value.accountDestination;
    if(operationType=='DEBIT')
    {
      this.accountService.debit(accountId,amount,description).subscribe({
        next:(data=>{
          alert("Success debit");
          this.handleSearchAccount();
        }),
        error:(err)=>{
          console.log(err);
        }
      })
    }
    else if(operationType=='CREDIT')
    {
      this.accountService.credit(accountId,amount,description).subscribe({
        next:(data=>{
          alert("Success credit");
          this.handleSearchAccount();
        }),
        error:(err)=> {
          console.log(err);
        }
      })
    }
    else if(operationType=='TRANSFER')
    {
      this.accountService.transfer(accountId,accountDestination,amount,description).subscribe({
        next:(data=>{
          alert("Success transfer");
          this.handleSearchAccount();
        }),
        error:(err)=> {
          console.log(err);
        }
      })
    }
    this.operationFormGroup.reset();
  }
}
