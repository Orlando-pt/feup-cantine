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
    private Double discount;
    private Long meatMeal;
    private Long fishMeal;
    private Long dietMeal;
    private Long vegetarianMeal;
    private Long desertMeal;
}
