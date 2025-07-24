package ma.enset.ebankingbackend.dtos;

import lombok.Data;

@Data
public class BankAccountRequestDTO {
    private double initialBalance;
    private double overdraft;
    private double interestRate;
    private String customerId;
    private String type;

}
