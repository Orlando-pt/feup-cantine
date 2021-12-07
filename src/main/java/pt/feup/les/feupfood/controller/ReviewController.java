package pt.feup.les.feupfood.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.feup.les.feupfood.dto.GetRestaurantReviewRequest;
import pt.feup.les.feupfood.dto.GetUserReviewRequest;
import pt.feup.les.feupfood.dto.SaveReviewRequest;
import pt.feup.les.feupfood.service.ReviewService;

@RestController
@CrossOrigin
@RequestMapping("/api/review/")
@Log4j2
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @PostMapping("/get/restaurant/review")
    public ResponseEntity getReviewsOfRestaurant(@RequestBody GetRestaurantReviewRequest getRestaurantReviewRequest) {
        return reviewService.getAllReviewsByRestaurant(getRestaurantReviewRequest.getRestaurantId());
    }
    @PostMapping("/get/user/review")
    public ResponseEntity getReviewsByUser(@RequestBody GetUserReviewRequest getUserReviewRequest) {
        return reviewService.getAllReviewsByRestaurant(getUserReviewRequest.getUserId());
    }

    @PostMapping("/save/review")
    public ResponseEntity saveReview(@RequestBody SaveReviewRequest saveReviewRequest) {
        return reviewService.saveReview(saveReviewRequest);
    }
}
