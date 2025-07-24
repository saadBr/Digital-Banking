package ma.enset.ebankingbackend.dtos;

import lombok.Data;
import ma.enset.ebankingbackend.enums.AccountStatus;

import java.util.Date;

@Data
public class SavingAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date dateCreated;
    private AccountStatus status;
    private String customerId;
    private double interestRate;
}
