package ma.enset.ebankingbackend.dtos;

import lombok.Data;
import ma.enset.ebankingbackend.enums.OperationType;

import java.util.Date;

@Data
public class AccountOperationDTO {
    private String id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
    private boolean cancelled;
}
