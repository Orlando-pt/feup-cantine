package pt.feup.les.feupfood.util;

import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Restaurant;

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

    public GetRestaurantDto parseRestaurantToRestaurantDto(Restaurant restaurant) {
        var restaurantDto = new GetRestaurantDto();
        restaurantDto.setId(restaurant.getId());
        // This condition might not be working
        restaurantDto.setFullName(restaurant.getOwner().getFullName());
        restaurantDto.setLocation(restaurant.getLocation());
        restaurantDto.setMorningOpeningSchedule(restaurant.getMorningOpeningSchedule());
        restaurantDto.setMorningClosingSchedule(restaurant.getMorningClosingSchedule());
        restaurantDto.setAfternoonOpeningSchedule(restaurant.getAfternoonOpeningSchedule());
        restaurantDto.setAfternoonClosingSchedule(restaurant.getAfternoonClosingSchedule());
        return restaurantDto;
    }
}
