package ma.enset.ebankingbackend.dtos;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String username;
    private String newPassword;
}
