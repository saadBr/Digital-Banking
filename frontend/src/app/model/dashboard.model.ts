export interface DashboardStats {
  accountCount: number;
  customerCount: number;
  totalBalance: number;
  operationCount: number;
  currentAccounts: number;
  savingAccounts: number;
  operationsByType?: {
    DEBIT: number;
    CREDIT: number;
    TRANSFER: number;
  };
}
