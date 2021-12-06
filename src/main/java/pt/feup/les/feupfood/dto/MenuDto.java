package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Schedule;

@Data
@NoArgsConstructor
public class MenuDto implements ResponseInterfaceDto {
    private Schedule schedule;
    private Meal meatMeal;
    private Meal fishMeal;
    private Meal dietMeal;
    private Meal vegetarianMeal;
}
