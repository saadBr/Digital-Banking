package ma.enset.ebankingbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.ebankingbackend.enums.AccountStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "bankAccounts")
@TypeAlias("bankAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BankAccount {
    @Id
    private String id;
    private double balance;
    private Date dateCreated;
    private AccountStatus status;
    private String customerId;
    private String createdByUserId;
}
