import {RouterModule, Routes} from '@angular/router';
import {Customers} from './customers/customers';
import {Accounts} from './accounts/accounts';
import {NgModule} from '@angular/core';
import {NewCustomer} from './new-customer/new-customer';

export const routes: Routes = [
  {path: "customers", component:Customers},
  {path: "accounts", component:Accounts},
  {path: "new-customer", component:NewCustomer}
];

@NgModule({
  imports:[RouterModule.forRoot(routes)],
  exports:[RouterModule]
})
export class AppRoutes {

}
