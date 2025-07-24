package ma.enset.ebankingbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bankAccounts")
@TypeAlias("savingAccount")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingAccount extends BankAccount {
    private double interestRate;
}
