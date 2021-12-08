 package pt.feup.les.feupfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pt.feup.les.feupfood.dto.GetUserReviewRequest;
import pt.feup.les.feupfood.dto.SaveReviewRequest;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;
import pt.feup.les.feupfood.repository.ReviewRepository;

@Service
public class ReviewService {

/*
    @Autowired
    ReviewRepository reviewRepository;

    public ResponseEntity getAllReviewsByRestaurant(Long restaurantId) {
        Restaurant restaurant = Restaurant.builder().restaurantId(restaurantId).build();
        return new ResponseEntity(reviewRepository.findAllByRestaurant(restaurant), HttpStatus.OK);
    }

    public ResponseEntity getAllReviewsByUserId(GetUserReviewRequest getReviewRequest) {
        DAOUser user = DAOUser.builder().userId(getReviewRequest.getUserId()).build();
        return new ResponseEntity(reviewRepository.findAllByUser(user), HttpStatus.OK);
    }

    public ResponseEntity saveReview(SaveReviewRequest saveReviewRequest) {
        DAOUser user = DAOUser.builder().userId(saveReviewRequest.getUserId()).build();
        Restaurant restaurant = Restaurant.builder().restaurantId(saveReviewRequest.getRestaurantId()).build();

        Review review = Review.builder().user(user).restaurant(restaurant).classificationGrade(saveReviewRequest.getClassificationGrade()).comment(saveReviewRequest.getComment()).build();
        if(reviewRepository.save(review) !=  null) {
            return new ResponseEntity("Review added successfully.", HttpStatus.OK);
        }
        return new ResponseEntity("Review could not be created.", HttpStatus.INTERNAL_SERVER_ERROR);

    }

     */
}
