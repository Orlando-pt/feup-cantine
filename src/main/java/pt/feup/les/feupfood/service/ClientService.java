package pt.feup.les.feupfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pt.feup.les.feupfood.dto.AddClientReviewDto;
import pt.feup.les.feupfood.dto.GetPutClientReviewDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.dto.IsFavoriteDto;
import pt.feup.les.feupfood.dto.PriceRangeDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.UpdateProfileDto;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;
import pt.feup.les.feupfood.repository.MenuRepository;
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

    @Autowired
    private MenuRepository menuRepository;

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
    public ResponseEntity<PriceRangeDto> getPriceRangeOfRestaurant(Long id) {
        PriceRangeDto priceRange = new PriceRangeDto();

        Restaurant restaurant = this.retrieveRestaurant(id);

        if (restaurant.getMeals().size() != 0) {
            priceRange.setMinimumPrice(
                this.menuRepository.findFirstByRestaurant(
                    restaurant, Sort.by(Direction.ASC, "startPrice")
                ).getStartPrice()
            );

            priceRange.setMaximumPrice(
                this.menuRepository.findFirstByRestaurant(
                    restaurant, Sort.by(Direction.DESC, "endPrice")
                ).getEndPrice()
            );
        } else {
            priceRange.setMaximumPrice(0.0);
            priceRange.setMinimumPrice(0.0);
        }


        return ResponseEntity.ok(priceRange);
    }

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

    // add favorite restaurant operations
    public ResponseEntity<String> addFavoriteRestaurant(
        Principal user,
        Long restaurantId
    ) {
        DAOUser client = this.retrieveUser(user.getName());
        
        List<Long> favoriteRestaurantIds = client.getClientFavoriteRestaurants().stream()
                    .map(restaurant -> restaurant.getId())
                    .collect(Collectors.toList());

        if (favoriteRestaurantIds.contains(restaurantId))
            return ResponseEntity.badRequest().body("Restaurant with id [" + restaurantId + "] is already on favorites list.");

        Restaurant restaurant = this.retrieveRestaurant(restaurantId);

        client.addFavoriteRestaurant(restaurant);
        this.userRepository.save(client);

        return ResponseEntity.ok("Operation made successfuly");
    }

    public ResponseEntity<String> removeFavoriteRestaurant(
        Principal user,
        Long restaurantId
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        List<Long> favoriteRestaurantIds = client.getClientFavoriteRestaurants().stream()
                    .map(restaurant -> restaurant.getId())
                    .collect(Collectors.toList());

        if (!favoriteRestaurantIds.contains(restaurantId))
            return ResponseEntity.badRequest().body("Restaurant with id [" + restaurantId + "] not on favorites list");

        Restaurant restaurant = this.retrieveRestaurant(restaurantId);

        client.removeFavoriteRestaurant(restaurant);
        this.userRepository.save(client);

        return ResponseEntity.ok("Operation made successfuly");
    }

    public ResponseEntity<List<GetRestaurantDto>> getFavoriteRestaurants(
        Principal user
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        ClientParser parser = new ClientParser();
        return ResponseEntity.ok(
            client.getClientFavoriteRestaurants().stream()
                .map(parser::parseRestaurantToRestaurantDto)
                .collect(Collectors.toList())
        );
    }

    public ResponseEntity<IsFavoriteDto> restaurantIsFavorite(
        Principal user,
        Long restaurantId
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        return ResponseEntity.ok( new IsFavoriteDto(
                !client.getClientFavoriteRestaurants().stream().filter(
                    restaurant -> restaurant.getId().equals(restaurantId)
                )
                    .collect(Collectors.toList()).isEmpty()
            )
        );
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
