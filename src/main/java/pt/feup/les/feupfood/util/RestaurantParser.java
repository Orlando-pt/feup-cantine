package pt.feup.les.feupfood.util;

import java.util.List;
import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;

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
        restaurantDto.setCuisines(restaurant.getCuisines());
        restaurantDto.setTypeMeals(restaurant.getTypeMeals());
        restaurantDto.setProfileImageUrl(restaurant.getOwner().getProfileImageUrl());
        restaurantDto.setMorningOpeningSchedule(restaurant.getMorningOpeningSchedule());
        restaurantDto.setMorningClosingSchedule(restaurant.getMorningClosingSchedule());
        restaurantDto.setAfternoonOpeningSchedule(restaurant.getAfternoonOpeningSchedule());
        restaurantDto.setAfternoonClosingSchedule(restaurant.getAfternoonClosingSchedule());
        return restaurantDto;
    }

    public GetPutMenuDto parseMenutoMenuDto(Menu menu) {
        GetPutMenuDto menuDto = new GetPutMenuDto();

        menuDto.setId(menu.getId());        
        menuDto.setName(menu.getName());
        menuDto.setAdditionalInformation(menu.getAdditionalInformation());
        menuDto.setStartPrice(menu.getStartPrice());
        menuDto.setEndPrice(menu.getEndPrice());
        
        this.addMeals(menuDto, menu.getMeals());
        return menuDto;
    }

    public GetAssignmentDto parseAssignmentToAssignmentDto(AssignMenu assignment) {
        GetAssignmentDto assignmentDto = new GetAssignmentDto();

        assignmentDto.setId(assignment.getId());
        assignmentDto.setDate(assignment.getDate());
        assignmentDto.setSchedule(assignment.getSchedule());
        assignmentDto.setMenu(parseMenutoMenuDto(assignment.getMenu()));

        return assignmentDto;
    }

    private void addMeals(GetPutMenuDto menuDto, List<Meal> meals) {
        meals.forEach(
            (meal) -> {
                if (meal.getMealType() == MealTypeEnum.MEAT)
                    menuDto.setMeatMeal(
                        parseMealtoMealDto(meal)
                    );
                else if (meal.getMealType() == MealTypeEnum.FISH)
                    menuDto.setFishMeal(
                        parseMealtoMealDto(meal)
                    );
                else if (meal.getMealType() == MealTypeEnum.DIET)
                    menuDto.setDietMeal(
                        parseMealtoMealDto(meal)
                    );
                else if (meal.getMealType() == MealTypeEnum.VEGETARIAN)
                    menuDto.setVegetarianMeal(
                        parseMealtoMealDto(meal)
                    );
                else if (meal.getMealType() == MealTypeEnum.DESERT)
                    menuDto.setDesertMeal(
                        parseMealtoMealDto(meal)
                    );
            }
        );
    }
}
