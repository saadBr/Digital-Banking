export interface AccountDetails {
  accountId:   string;
  balance:     number;
  currentPage: number;
  pageSize:    number;
  totalPages:  number;
  operations:  Operation[];
}

export interface Operation {
  id:            number;
  operationDate: Date;
  amount:        number;
  type:          string;
  description:   string;
}
