export interface AccountDetails {
  id: string;
  balance: number;
  dateCreated: string;
  customerId: string;
  status: string;
  type: string;
  interestRate?: number;
  overdraftLimit?: number;
  operations: Operation[];
  currentPage: number;
  pageSize: number;
  totalPages: number;
}

export interface Operation {
  id: number;
  operationDate: Date;
  amount: number;
  type: string;
  description: string;
  cancelled?: boolean;
}
