package ma.enset.ebankingbackend.dtos;

import jakarta.persistence.*;
import lombok.Data;
import ma.enset.ebankingbackend.enums.AccountStatus;

import java.util.Date;


@Data
public class CurrentAccountDTO extends BankAccountDTO {
    @Id
    private String id;
    private double balance;
    private Date dateCreated;
    private AccountStatus status;
    private CustomerDTO customer;
    private double overdraftLimit;
}
