package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserResponseDto {
    private String fullName;
    private String email;
    private String role;
}
