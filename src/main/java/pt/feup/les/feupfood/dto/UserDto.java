package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private String fullName;
    private String password;
    private String email;
    private String role;
    private Boolean terms;
    
}
