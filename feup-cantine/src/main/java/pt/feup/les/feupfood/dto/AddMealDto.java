package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.feup.les.feupfood.model.MealTypeEnum;

@Data
@NoArgsConstructor
public class AddMealDto implements ResponseInterfaceDto{
    
    private MealTypeEnum mealType;
    private String description;
    private String nutritionalInformation;
}
