package pt.feup.les.feupfood.util;

import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.Meal;
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

    public GetPutMenuDto parseMenutoMenuDto(Menu menu) {
        GetPutMenuDto menuDto = new GetPutMenuDto();

        menuDto.setId(menu.getId());        
        menuDto.setName(menu.getName());
        menuDto.setAdditionalInformaiton(menu.getAdditionalInformation());
        menuDto.setStartPrice(menu.getStartPrice());
        menuDto.setEndPrice(menu.getEndPrice());
        
        // TODO modify this way of parsing things
        menuDto.setMeatMeal(
            parseMealtoMealDto(menu.getMeals().get(0))
        );

        menuDto.setFishMeal(
            parseMealtoMealDto(menu.getMeals().get(1))
        );

        menuDto.setDietMeal(
            parseMealtoMealDto(menu.getMeals().get(2))
        );

        menuDto.setVegetarianMeal(
            parseMealtoMealDto(menu.getMeals().get(3))
        );

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
}
