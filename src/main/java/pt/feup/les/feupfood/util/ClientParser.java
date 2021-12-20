package pt.feup.les.feupfood.util;

import pt.feup.les.feupfood.dto.GetPutClientReviewDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;

public class ClientParser {
    public GetPutClientReviewDto parseReviewToReviewDto(Review review) {
        var reviewDto = new GetPutClientReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setClientId(review.getClient().getId());
        reviewDto.setClientFullName(review.getClient().getFullName());
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
        getRestaurantDto.setProfileImageUrl(restaurant.getProfileImageUrl());
        getRestaurantDto.setMorningOpeningSchedule(restaurant.getMorningOpeningSchedule());
        getRestaurantDto.setMorningClosingSchedule(restaurant.getMorningClosingSchedule());
        getRestaurantDto.setAfternoonOpeningSchedule(restaurant.getAfternoonOpeningSchedule());
        getRestaurantDto.setAfternoonClosingSchedule(restaurant.getAfternoonClosingSchedule());
        return getRestaurantDto;
    }
}
