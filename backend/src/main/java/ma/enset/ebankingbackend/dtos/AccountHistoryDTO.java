package ma.enset.ebankingbackend.dtos;

import lombok.Data;
import ma.enset.ebankingbackend.enums.AccountStatus;

import java.util.List;

@Data
public class AccountHistoryDTO {
    private String accountId;
    private double balance;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private List<AccountOperationDTO> operations;
    private AccountStatus status;
    private String type;
    private double interestRate;
    private double overdraftLimit;
}
