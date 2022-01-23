package pt.feup.les.feupfood.util;

import java.util.List;
import java.util.stream.Collectors;

import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.GetClientEatIntention;
import pt.feup.les.feupfood.dto.GetClientReviewDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.dto.UpdateProfileDto;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;

public class ClientParser {

    private RestaurantParser restaurantParser;

    public ClientParser() {
        this.restaurantParser = new RestaurantParser();
    }

    public UpdateProfileDto parseUserProfile(DAOUser user) {
        UpdateProfileDto profileDto = new UpdateProfileDto();
        profileDto.setBiography(user.getBiography());
        profileDto.setFullName(user.getFullName());
        profileDto.setProfileImageUrl(user.getProfileImageUrl());

        return profileDto;
    }

    public GetClientReviewDto parseReviewToReviewDto(Review review) {
        var reviewDto = new GetClientReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setClientId(review.getClient().getId());
        reviewDto.setClientFullName(review.getClient().getFullName());
        reviewDto.setClientProfileImageUrl(review.getClient().getProfileImageUrl());
        reviewDto.setRestaurantId(review.getRestaurant().getId());
        reviewDto.setClassificationGrade(review.getClassificationGrade());
        reviewDto.setComment(review.getComment());
        reviewDto.setAnswer(review.getAnswer());
        reviewDto.setTimestamp(review.getTimestamp());
        return reviewDto;
    }

    public GetRestaurantDto parseRestaurantToRestaurantDto(Restaurant restaurant) {
        var getRestaurantDto = new GetRestaurantDto();
        getRestaurantDto.setId(restaurant.getId());
        getRestaurantDto.setFullName(restaurant.getOwner().getFullName());
        getRestaurantDto.setLocation(restaurant.getLocation());
        getRestaurantDto.setCuisines(restaurant.getCuisines());
        getRestaurantDto.setTypeMeals(restaurant.getTypeMeals());
        getRestaurantDto.setProfileImageUrl(restaurant.getOwner().getProfileImageUrl());
        getRestaurantDto.setMorningOpeningSchedule(restaurant.getMorningOpeningSchedule());
        getRestaurantDto.setMorningClosingSchedule(restaurant.getMorningClosingSchedule());
        getRestaurantDto.setAfternoonOpeningSchedule(restaurant.getAfternoonOpeningSchedule());
        getRestaurantDto.setAfternoonClosingSchedule(restaurant.getAfternoonClosingSchedule());
        return getRestaurantDto;
    }

    public GetClientEatIntention parseEatIntentionToDto(EatIntention intention) {
        GetClientEatIntention intentionDto = new GetClientEatIntention();
        intentionDto.setId(intention.getId());
        intentionDto.setAssignment(
            this.restaurantParser.parseAssignmentToAssignmentDto(intention.getAssignment())
        );

        intentionDto.setMeals(
            intention.getMeals().stream()
                .map(restaurantParser::parseMealtoMealDto)
                .collect(Collectors.toSet())
        );

        intentionDto.setCode(intention.getCode());
        intentionDto.setValidatedCode(intention.getValidatedCode());
        intentionDto.setRestaurant(intention.getAssignment().getRestaurant().getOwner().getFullName());

        intentionDto.getAssignment().setPurchased(true);
        
        List<Long> mealIds = intention.getMeals().stream()
                                .map(Meal::getId)
                                .collect(Collectors.toList());

        this.checkIfMealsAreInsideList(intentionDto.getAssignment(), mealIds);

        return intentionDto;
    }

    public GetAssignmentDto parseAssignmentToAssignmentDto(AssignMenu assignment, DAOUser client) {
        GetAssignmentDto assignmentDto = this.restaurantParser.parseAssignmentToAssignmentDto(assignment);

        assignmentDto.setPurchased(
            client.getEatingIntentions().stream()
                .anyMatch(
                    intention -> intention.getAssignment().getId().equals(
                        assignmentDto.getId()
                    )
                )
        );

        if (assignmentDto.getPurchased()) {
            // verify which meals were choosen
            List<Long> mealIds = client.getEatingIntentions()
                .stream().filter(
                    intention -> intention.getAssignment().getId().equals(
                        assignmentDto.getId()
                    )
                ).collect(Collectors.toList())
                .get(0)
                .getMeals()
                .stream().map(
                    Meal::getId
                ).collect(Collectors.toList());

            this.checkIfMealsAreInsideList(assignmentDto, mealIds);
        }

        return assignmentDto;
    }

    private GetAssignmentDto checkIfMealsAreInsideList(GetAssignmentDto assignmentDto, List<Long> mealIds){
        if (assignmentDto.getMenu().getDesertMeal() != null)
            assignmentDto.getMenu().getDesertMeal().setChoosen(
                mealIds.contains(
                    assignmentDto.getMenu().getDesertMeal().getId()
                )
            );

        if (assignmentDto.getMenu().getDietMeal() != null)
            assignmentDto.getMenu().getDietMeal().setChoosen(
                mealIds.contains(
                    assignmentDto.getMenu().getDietMeal().getId()
                )
            );

        if (assignmentDto.getMenu().getFishMeal() != null)
            assignmentDto.getMenu().getFishMeal().setChoosen(
                mealIds.contains(
                    assignmentDto.getMenu().getFishMeal().getId()
                )
            );

        if (assignmentDto.getMenu().getMeatMeal() != null)
            assignmentDto.getMenu().getMeatMeal().setChoosen(
                mealIds.contains(
                    assignmentDto.getMenu().getMeatMeal().getId()
                )
            );

        if (assignmentDto.getMenu().getVegetarianMeal() != null)
            assignmentDto.getMenu().getVegetarianMeal().setChoosen(
                mealIds.contains(
                    assignmentDto.getMenu().getVegetarianMeal().getId()
                )
            );
        return assignmentDto;
    }
}
