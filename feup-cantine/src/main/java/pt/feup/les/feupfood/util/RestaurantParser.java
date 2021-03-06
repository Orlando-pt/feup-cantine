package pt.feup.les.feupfood.util;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.dto.VerifyCodeDto;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
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

        mealDto.setChoosen(false);
        return mealDto;
    }

    public GetRestaurantDto parseRestaurantToRestaurantDto(Restaurant restaurant) {
        var restaurantDto = new GetRestaurantDto();
        restaurantDto.setId(restaurant.getId());
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
        menuDto.setDiscount(menu.getDiscount());
        
        this.addMeals(menuDto, menu.getMeals());
        return menuDto;
    }

    public GetAssignmentDto parseAssignmentToAssignmentDto(AssignMenu assignment) {
        GetAssignmentDto assignmentDto = new GetAssignmentDto();

        assignmentDto.setId(assignment.getId());
        assignmentDto.setDate(assignment.getDate());
        assignmentDto.setSchedule(assignment.getSchedule());
        assignmentDto.setMenu(parseMenutoMenuDto(assignment.getMenu()));
        assignmentDto.setNumberOfIntentions(assignment.getEatingIntentions().size());
        assignmentDto.setAvailable(
            this.verifyAvailabilityOfAssignment(assignment.getDate())
        );

        assignmentDto.setPurchased(false);

        this.addMealIntentions(assignment, assignmentDto);

        return assignmentDto;
    }

    public VerifyCodeDto parseUserToVerifyCodeDto(DAOUser user, Set<Meal> meals) {
        VerifyCodeDto dto = new VerifyCodeDto();

        dto.setFullName(user.getFullName());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setMeals(
            meals.stream().map(
                this::parseMealtoMealDto
            ).collect(Collectors.toSet())
        );

        return dto;
    }

    private GetAssignmentDto addMealIntentions(AssignMenu assignment, GetAssignmentDto assignmentDto) {
        if (assignmentDto.getMenu().getDesertMeal() != null)
            assignmentDto.getMenu().getDesertMeal().setNumberOfIntentions(
                (int) this.retrieveMeal(
                    assignment.getMenu().getMeals(),
                    MealTypeEnum.DESERT
                ).getEatingIntentions().stream()
                    .filter(
                        intention -> intention.getAssignment().equals(assignment)
                    ).count()
            );

        if (assignmentDto.getMenu().getDietMeal() != null)
            assignmentDto.getMenu().getDietMeal().setNumberOfIntentions(
                (int) this.retrieveMeal(
                    assignment.getMenu().getMeals(),
                    MealTypeEnum.DIET
                ).getEatingIntentions().stream()
                    .filter(
                        intention -> intention.getAssignment().equals(assignment)
                    ).count()
            );

        if (assignmentDto.getMenu().getFishMeal() != null)
            assignmentDto.getMenu().getFishMeal().setNumberOfIntentions(
                (int) this.retrieveMeal(
                    assignment.getMenu().getMeals(),
                    MealTypeEnum.FISH
                ).getEatingIntentions().stream()
                    .filter(
                        intention -> intention.getAssignment().equals(assignment)
                    ).count()
            );

        if (assignmentDto.getMenu().getMeatMeal() != null)
            assignmentDto.getMenu().getMeatMeal().setNumberOfIntentions(
                (int) this.retrieveMeal(
                    assignment.getMenu().getMeals(),
                    MealTypeEnum.MEAT
                ).getEatingIntentions().stream()
                    .filter(
                        intention -> intention.getAssignment().equals(assignment)
                    ).count()
            );

        if (assignmentDto.getMenu().getVegetarianMeal() != null)
            assignmentDto.getMenu().getVegetarianMeal().setNumberOfIntentions(
                (int) this.retrieveMeal(
                    assignment.getMenu().getMeals(),
                    MealTypeEnum.VEGETARIAN
                ).getEatingIntentions().stream()
                    .filter(
                        intention -> intention.getAssignment().equals(assignment)
                    ).count()
            );
        return assignmentDto;
    }

    private Meal retrieveMeal(List<Meal> meals, MealTypeEnum mealType) {
        for (Meal meal : meals)
            if (meal.getMealType() == mealType)
                return meal;

        return new Meal();
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

    private boolean verifyAvailabilityOfAssignment(Date assignmentDate) {
        long oneDay = 1000L * 60 * 60 * 24;
        Date tomorrow = new Date(System.currentTimeMillis() + oneDay);

        if (tomorrow.after(assignmentDate))
            return false;

        return true;
    }
}
