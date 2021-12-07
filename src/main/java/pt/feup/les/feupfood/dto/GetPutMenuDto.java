package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetPutMenuDto implements ResponseInterfaceDto{

    private Long id;
    private String name;
    private String additionalInformaiton;
    private Double startPrice;
    private Double endPrice;
    private GetPutMealDto meatMeal;
    private GetPutMealDto fishMeal;
    private GetPutMealDto dietMeal;
    private GetPutMealDto vegetarianMeal;

    // TODO add assignments

}
