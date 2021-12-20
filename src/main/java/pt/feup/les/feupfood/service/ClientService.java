package pt.feup.les.feupfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pt.feup.les.feupfood.dto.AddClientReviewDto;
import pt.feup.les.feupfood.dto.GetPutClientReviewDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.UpdateProfileDto;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
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

    // profile operations
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
        daoUser.setProfileImageUrl(profileDto.getProfileImageUrl());

        daoUser = this.userRepository.save(daoUser);
        
        return ResponseEntity.ok(
            new ClientParser().parseUserProfile(daoUser)
        );
    }

    // review operations
    public ResponseEntity<ResponseInterfaceDto> saveReviewsFromRestaurantByRestaurantId(Long id, AddClientReviewDto clientReviewDto, Principal user) {
        DAOUser reviewer = this.retrieveUser(user.getName());
        Restaurant reviewedRestaurant = this.retrieveRestaurant(id);
        Review review = new Review();

        review.setClient(reviewer);
        review.setClassificationGrade(clientReviewDto.getClassificationGrade());
        review.setComment(clientReviewDto.getComment());
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

    // restaurant operations
    public ResponseEntity<List<GetRestaurantDto>> getAllRestaurants() {
        ClientParser clientParser = new ClientParser();
        return ResponseEntity.ok(this.restaurantRepository.findAll().stream().map(clientParser::parseRestaurantToRestaurantDto).collect(Collectors.toList()));
    }

    public ResponseEntity<List<GetPutClientReviewDto>> getReviewsFromRestaurantByRestaurantId(Long id) {
        Restaurant reviewedRestaurant = this.retrieveRestaurant(id);

        return ResponseEntity.ok(this.getAllReviewsFromRestaurant(reviewedRestaurant));
    }

    public ResponseEntity<ResponseInterfaceDto> getRestaurantById(Long restaurantId) {

        Restaurant restaurant = this.restaurantRepository.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("The restaurant id was not found"));

        return ResponseEntity.ok(new ClientParser().parseRestaurantToRestaurantDto(restaurant));
    }

    // auxiliar methods
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

    private List<GetPutClientReviewDto> getAllReviewsFromRestaurant(Restaurant restaurant) {
        ClientParser clientParser = new ClientParser();
        return restaurant.getReviews().stream().map(clientParser::parseReviewToReviewDto).collect(Collectors.toList());
    }
}
