package pt.feup.les.feupfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pt.feup.les.feupfood.dto.*;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.ReviewRepository;
import pt.feup.les.feupfood.repository.UserRepository;
import pt.feup.les.feupfood.util.ClientParser;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public ResponseEntity<UpdateProfileDto> getProfile(
        Principal user
    ) {
        return ResponseEntity.ok(
            new ClientParser().parseUserProfile(
                this.retrieveUser(user.getName())
            )
        );
    }

    public ResponseEntity<UpdateProfileDto> updateProfile(
        Principal user,
        UpdateProfileDto profileDto
    ) {
        DAOUser daoUser = this.retrieveUser(user.getName());

        daoUser.setFullName(profileDto.getFullName());
        daoUser.setBiography(profileDto.getBiography());

        daoUser = this.userRepository.save(daoUser);
        
        return ResponseEntity.ok(
            new ClientParser().parseUserProfile(daoUser)
        );
    }

    private DAOUser retrieveUser(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private Restaurant retrieveRestaurant(Long restaurantId) {
        return this.restaurantRepository.findById(restaurantId).orElseThrow(() -> new UsernameNotFoundException("Restaurant not found with id :" + restaurantId));
    }

    private List<GetPutClientReviewDto> getAllReviewsFromClient(DAOUser client) {
        ClientParser clientParser = new ClientParser();
        return client.getReviews().stream().map(clientParser::parseReviewToReviewDto).collect(Collectors.toList());
    }

    public ResponseEntity<ResponseInterfaceDto> saveReview(Principal user, AddClientReviewDto reviewDto) {

        DAOUser reviewer = this.retrieveUser(user.getName());
        Restaurant reviewedRestaurant = this.retrieveRestaurant(reviewDto.getRestaurantId());
        Review review = new Review();

        review.setClient(reviewer);
        review.setClassificationGrade(reviewDto.getClassificationGrade());
        review.setComment(reviewDto.getComment());
        review.setRestaurant(reviewedRestaurant);

        review = this.reviewRepository.save(review);

        // add review to the user
        reviewer.addReview(review);
        // add review to the restaurant
        reviewedRestaurant.addReview(review);

        this.userRepository.save(reviewer);
        this.restaurantRepository.save(reviewedRestaurant);

        return ResponseEntity.ok(new ClientParser().parseReviewToReviewDto(review));
    }

    public ResponseEntity<List<GetPutClientReviewDto>> getUserReviewsFromClient(Principal user) {

        DAOUser reviewer = this.retrieveUser(user.getName());

        return ResponseEntity.ok(this.getAllReviewsFromClient(reviewer));
    }

    public ResponseEntity<List<GetRestaurantDto>> getAllRestaurants() {
        ClientParser clientParser = new ClientParser();
        return ResponseEntity.ok(this.restaurantRepository.findAll().stream().map(clientParser::parseRestaurantToRestaurantDto).collect(Collectors.toList()));
    }
}
