import { RouterModule, Routes } from '@angular/router';
import { Customers } from './customers/customers';
import { Accounts } from './accounts/accounts';
import { NgModule } from '@angular/core';
import { NewCustomer } from './new-customer/new-customer';
import { CustomerAccounts } from './customer-accounts/customer-accounts';
import { Login } from './login/login';
import { AdminTemplate } from './admin-template/admin-template';
import { authenticationGuard } from './guards/authentication-guard';
import { authorizationGuard } from './guards/authorization-guard';
import { NotAutorized } from './not-autorized/not-autorized';
import { Dashboard } from './dashboard/dashboard';
import { NewAccount } from './new-account/new-account';
import { ChangePassword } from './change-password/change-password';
import { NewUser } from './new-user/new-user';
import { ManageUser } from './manage-user/manage-user';
import { LogViewer } from './log-viewer/log-viewer';
export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: Login },
  {
    path: 'admin',
    component: AdminTemplate,
    canActivate: [authenticationGuard],
    children: [
      { path: 'customers', component: Customers },
      { path: 'accounts', component: Accounts },
      { path: 'new-customer', component: NewCustomer },
      { path: 'customer-accounts/:id', component: CustomerAccounts },
      { path: 'notAuthorized', component: NotAutorized },
      { path: 'dashboard', component: Dashboard },
      { path: 'customer-accounts/:id/new-account', component: NewAccount },
      { path: 'change-password', component: ChangePassword },
      {
        path: 'create-user',
        component: NewUser,
        canActivate: [authorizationGuard],
        data: { role: ['ADMIN'] },
      },
      {
        path: 'manage-users',
        component: ManageUser,
        canActivate: [authorizationGuard],
        data: { role: ['ADMIN'] },
      },
      {
        path: 'logs',
        component: LogViewer,
        canActivate: [authorizationGuard],
        data: { role: ['ADMIN'] },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutes {}
