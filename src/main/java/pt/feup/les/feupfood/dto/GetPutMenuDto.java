package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetPutMenuDto implements ResponseInterfaceDto{

    private Long id;
    private String name;
    private String additionalInformation;
    private Double startPrice;
    private Double endPrice;
    private GetPutMealDto meatMeal;
    private GetPutMealDto fishMeal;
    private GetPutMealDto dietMeal;
    private GetPutMealDto vegetarianMeal;
    private GetPutMealDto desertMeal;

    // private RestaurantProfileDto restaurant;
    // TODO add assignments

}
