package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateProfileDto {
    private String fullName;
    private String biography;
}
