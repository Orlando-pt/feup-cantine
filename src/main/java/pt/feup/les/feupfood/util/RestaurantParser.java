package pt.feup.les.feupfood.util;

import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.model.Meal;

public class RestaurantParser {

    public Meal parseAddMealDtoToMeal(AddMealDto mealDto) {
        var meal = new Meal();
        meal.setDescription(mealDto.getDescription());
        meal.setMealType(mealDto.getMealType());
        meal.setNutritionalInformation(mealDto.getNutritionalInformation());

        return meal;
    }

    public GetPutMealDto parseMealtoMealDto(Meal meal) {
        var mealDto = new GetPutMealDto();
        mealDto.setDescription(meal.getDescription());
        mealDto.setId(meal.getId());
        mealDto.setMealType(meal.getMealType());
        mealDto.setNutritionalInformation(meal.getNutritionalInformation());
        return mealDto;
    }
    
}
