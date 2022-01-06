package pt.feup.les.feupfood.util;

import java.util.stream.Collectors;

import pt.feup.les.feupfood.dto.GetClientEatIntention;
import pt.feup.les.feupfood.dto.GetPutClientReviewDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.dto.UpdateProfileDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;

public class ClientParser {

    public UpdateProfileDto parseUserProfile(DAOUser user) {
        UpdateProfileDto profileDto = new UpdateProfileDto();
        profileDto.setBiography(user.getBiography());
        profileDto.setFullName(user.getFullName());
        profileDto.setProfileImageUrl(user.getProfileImageUrl());

        return profileDto;
    }

    public GetPutClientReviewDto parseReviewToReviewDto(Review review) {
        var reviewDto = new GetPutClientReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setClientId(review.getClient().getId());
        reviewDto.setClientFullName(review.getClient().getFullName());
        reviewDto.setClientProfileImageUrl(review.getClient().getProfileImageUrl());
        reviewDto.setRestaurantId(review.getRestaurant().getId());
        reviewDto.setClassificationGrade(review.getClassificationGrade());
        reviewDto.setComment(review.getComment());
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
        RestaurantParser restaurantParser = new RestaurantParser();

        GetClientEatIntention intentionDto = new GetClientEatIntention();
        intentionDto.setId(intention.getId());
        intentionDto.setAssignment(
            restaurantParser.parseAssignmentToAssignmentDto(intention.getAssignment())
        );

        intentionDto.setMeals(
            intention.getMeals().stream()
                .map(restaurantParser::parseMealtoMealDto)
                .collect(Collectors.toSet())
        );

        intentionDto.setCode(intention.getCode());
        intentionDto.setValidatedCode(intention.getValidatedCode());

        return intentionDto;
    }
}
