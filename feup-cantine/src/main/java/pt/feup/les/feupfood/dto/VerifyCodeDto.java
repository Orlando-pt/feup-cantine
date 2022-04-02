package pt.feup.les.feupfood.dto;

import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerifyCodeDto {
    private String fullName;
    private String profileImageUrl;
    private Set<GetPutMealDto> meals;
}
