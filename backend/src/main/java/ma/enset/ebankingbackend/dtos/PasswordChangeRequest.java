package ma.enset.ebankingbackend.dtos;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}
