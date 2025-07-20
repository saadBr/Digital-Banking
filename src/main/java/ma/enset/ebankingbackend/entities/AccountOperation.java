package ma.enset.ebankingbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.ebankingbackend.enums.OperationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "accountOperations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOperation {
    @Id
    private String id;

    private Date operationDate;
    private double amount;
    private OperationType type;

    private String bankAccountId;
    private String description;
    private String performedByUserId;

    private boolean cancelled = false;
}
