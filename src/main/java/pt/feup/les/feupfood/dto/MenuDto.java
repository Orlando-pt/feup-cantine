package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.ScheduleEnum;

@Data
@NoArgsConstructor
public class MenuDto implements ResponseInterfaceDto {
    private ScheduleEnum schedule;
    private Meal meatMeal;
    private Meal fishMeal;
    private Meal dietMeal;
    private Meal vegetarianMeal;
}
