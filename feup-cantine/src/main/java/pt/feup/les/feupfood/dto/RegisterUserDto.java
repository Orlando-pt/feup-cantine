package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserDto {
 
    private String fullName;
    private String password;
    private String confirmPassword;
    private String email;
    private Boolean terms;
}
