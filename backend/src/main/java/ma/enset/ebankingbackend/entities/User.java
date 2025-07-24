package ma.enset.ebankingbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.ebankingbackend.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String id;

    private String username;
    private String password;
    private String email;
    private Boolean enabled;

    private Set<Role> roles = new HashSet<>();
}
