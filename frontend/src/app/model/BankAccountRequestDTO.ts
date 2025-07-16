export interface BankAccountRequestDTO {
  initialBalance: number;
  overdraft?: number;
  interestRate?: number;
  customerId: number;
  type: 'current' | 'saving';
}
