import {RouterModule, Routes} from '@angular/router';
import {Customers} from './customers/customers';
import {Accounts} from './accounts/accounts';
import {NgModule} from '@angular/core';
import {NewCustomer} from './new-customer/new-customer';
import {CustomerAccounts} from './customer-accounts/customer-accounts';

export const routes: Routes = [
  {path: "customers", component:Customers},
  {path: "accounts", component:Accounts},
  {path: "new-customer", component:NewCustomer},
  {path: "customer-accounts/:id", component:CustomerAccounts}
];

@NgModule({
  imports:[RouterModule.forRoot(routes)],
  exports:[RouterModule]
})
export class AppRoutes {

}
