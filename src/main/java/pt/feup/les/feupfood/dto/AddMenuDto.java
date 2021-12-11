package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddMenuDto implements ResponseInterfaceDto {
    private String name;
    private String additionalInformation;
    private Double startPrice;
    private Double endPrice;
    private Long meatMealId;
    private Long fishMealId;
    private Long dietMealId;
    private Long vegetarianMealId;
    private Long desertMealId;
}
