package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.feup.les.feupfood.model.MealTypeEnum;

@Data
@NoArgsConstructor
public class GetPutMealDto implements ResponseInterfaceDto{
    
    private Long id;
    private MealTypeEnum mealType;
    private String description;
    private String nutritionalInformation;
    private int numberOfIntentions;
}
