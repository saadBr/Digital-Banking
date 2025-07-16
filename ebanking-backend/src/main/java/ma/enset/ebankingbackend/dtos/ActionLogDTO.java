package ma.enset.ebankingbackend.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ActionLogDTO {
    private Long id;
    private Date timestamp;
    private String action;
    private String description;
    private String performedBy;
}
