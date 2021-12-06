package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.feup.les.feupfood.model.MealType;
import pt.feup.les.feupfood.model.Restaurant;

@Data
@NoArgsConstructor
public class MealDto implements ResponseInterfaceDto {
    private Restaurant restaurant;
    private MealType mealType;
    private String description;
    private String nutritionalInformation;
}
