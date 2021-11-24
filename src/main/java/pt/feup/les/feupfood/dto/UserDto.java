package pt.feup.les.feupfood.dto;

import lombok.Data;

@Data
public class UserDto {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String role;
    
}
