package pt.feup.les.feupfood.util;

import pt.feup.les.feupfood.dto.GetPutClientReviewDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.dto.UpdateProfileDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;

public class ClientParser {

    public UpdateProfileDto parseUserProfile(DAOUser user) {
        UpdateProfileDto profileDto = new UpdateProfileDto();
        profileDto.setBiography(user.getBiography());
        profileDto.setFullName(user.getFullName());

        return profileDto;
    }

    public GetPutClientReviewDto parseReviewToReviewDto(Review review) {
        var reviewDto = new GetPutClientReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setClientId(review.getClient().getId());
        reviewDto.setRestaurantId(review.getRestaurant().getId());
        reviewDto.setClassificationGrade(review.getClassificationGrade());
        reviewDto.setComment(review.getComment());
        return reviewDto;
    }

    public GetRestaurantDto parseRestaurantToRestaurantDto(Restaurant restaurant) {
        var getRestaurantDto = new GetRestaurantDto();
        getRestaurantDto.setId(restaurant.getId());
        getRestaurantDto.setLocation(restaurant.getLocation());
        getRestaurantDto.setMorningOpeningSchedule(restaurant.getMorningOpeningSchedule());
        getRestaurantDto.setMorningClosingSchedule(restaurant.getMorningClosingSchedule());
        getRestaurantDto.setAfternoonOpeningSchedule(restaurant.getAfternoonOpeningSchedule());
        getRestaurantDto.setAfternoonClosingSchedule(restaurant.getAfternoonClosingSchedule());
        return getRestaurantDto;
    }
}
