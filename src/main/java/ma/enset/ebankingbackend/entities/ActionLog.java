package ma.enset.ebankingbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "actionLogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionLog {
    @Id
    private String id;

    private Date timestamp;
    private String action;
    private String description;

    private String performedByUserId;
}
