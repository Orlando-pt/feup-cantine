package pt.feup.les.feupfood.controller;

import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pt.feup.les.feupfood.dto.*;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.Meal;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientController_RestTemplateIT {
    
    @LocalServerPort
    int RANDOM_PORT;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    private RegisterUserDto clientUser;
    private String token;
    
    public ClientController_RestTemplateIT() {
        this.clientUser = new RegisterUserDto();
        
        this.clientUser.setEmail("alzira@mail.com");
        this.clientUser.setPassword("secretPassword");
        this.clientUser.setConfirmPassword("secretPassword");
        this.clientUser.setFullName("Alzira");
        this.clientUser.setTerms(true);
    }
    
    @BeforeAll
    public void setup() {
        this.registerClient();
        this.token = this.authenticateClient().getJwttoken();
    }
    
    @Test
    void callClientHome() {
        var headers = this.getStandardHeaders();
        
        var response = this.restTemplate.exchange("/api/client/home", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        Assertions.assertThat(response.getBody()).isEqualTo("Hello client!");
    }
    
    @Test
    void updateProfileTest() {
        var headers = this.getStandardHeaders();

        var profileDto = new UpdateProfileDto();
        profileDto.setBiography("biography");
        profileDto.setFullName("A Brand new full name.");
        
        var updateProfile = this.restTemplate.exchange(
            "/api/client/profile",
            HttpMethod.PUT,
            new HttpEntity<>(profileDto, headers),
            UpdateProfileDto.class
        );

        Assertions.assertThat(
            updateProfile.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            updateProfile.getBody()
        ).isEqualTo(profileDto);

        var getProfile = this.restTemplate.exchange(
            "/api/client/profile",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            UpdateProfileDto.class
        );

        Assertions.assertThat(
            getProfile.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getProfile.getBody()
        ).isEqualTo(profileDto);
        
    }
    
    @Test
    void addGetAndPostClientReview() {

        RegisterUserDto restaurantUser = new RegisterUserDto();
        
        restaurantUser.setEmail("fjforbas@gmail.com");
        restaurantUser.setPassword("secretPassword");
        restaurantUser.setConfirmPassword("secretPassword");
        restaurantUser.setFullName("ajsbfasjbfas");
        restaurantUser.setTerms(true);
        
        this.restTemplate.postForEntity("/api/restaurant/register", restaurantUser, RegisterUserResponseDto.class);
        
        var headers = this.getStandardHeaders();
        
        var getRestaurantId = this.restTemplate.exchange("/api/client/restaurant", HttpMethod.GET, new HttpEntity<>(headers), GetRestaurantDto[].class);
        
        AddClientReviewDto reviewDto = new AddClientReviewDto();
        reviewDto.setClassificationGrade(5);
        reviewDto.setComment("Very good food!");

        AddClientReviewDto reviewDto2 = new AddClientReviewDto();
        reviewDto2.setClassificationGrade(2);
        reviewDto2.setComment("Very bad food!");

        var addReview = this.restTemplate.exchange("/api/client/review/restaurant/" + getRestaurantId.getBody()[0].getId(), HttpMethod.POST, new HttpEntity<>(reviewDto, headers), GetPutClientReviewDto.class);

        var addReview2 = this.restTemplate.exchange("/api/client/review/restaurant/" + getRestaurantId.getBody()[0].getId(),
                HttpMethod.POST, new HttpEntity<>(reviewDto, headers), GetPutClientReviewDto.class);

        Assertions.assertThat(addReview.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(addReview2.getStatusCode()).isEqualTo(HttpStatus.OK);

        var getReviews = this.restTemplate.exchange("/api/client/review", HttpMethod.GET, new HttpEntity<>(headers), GetPutClientReviewDto[].class);
        
        Assertions.assertThat(getReviews.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(getReviews.getBody()).hasSize(2).contains(addReview.getBody());

    }

    @Test
    void getRestaurantByIdAndGetReviewsOfTheRestaurant() {
        var headers = this.getStandardHeaders();

        var getRestaurants = this.restTemplate.exchange(
                "/api/client/restaurant",
                HttpMethod.GET, new HttpEntity<>(headers),
                GetRestaurantDto[].class);
        
        Long id = getRestaurants.getBody()[0].getId();

        var getRestaurantById = this.restTemplate.exchange(
                "/api/client/restaurant/" + id,
                HttpMethod.GET, new HttpEntity<>(headers),
                GetRestaurantDto.class);

        Assertions.assertThat(getRestaurantById.getStatusCode()).isEqualTo(HttpStatus.OK);

        var getReviewsByRestaurantId = this.restTemplate.exchange(
                "/api/client/review/restaurant/" + id,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                GetPutClientReviewDto[].class);
        
        GetPutClientReviewDto expectedReview = new GetPutClientReviewDto();
        expectedReview.setClassificationGrade(2);
        expectedReview.setClientFullName("Francisco Bastos");
        expectedReview.setClientId(4L);
        expectedReview.setId(2L);
        expectedReview.setClientProfileImageUrl("https://media.istockphoto.com/photos/strong-" + 
        "real-person-real-body-senior-man-proudly-flexing-muscles-picture-id638471524?s=612x612");
        expectedReview.setRestaurantId(1L);
        expectedReview.setComment("Who does not like a meal of rice with potato sauce");

        Assertions.assertThat(getReviewsByRestaurantId.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getReviewsByRestaurantId.getBody()
        ).contains(expectedReview);
    }
    
    @Test
    void testRestaurantPriceRange() {
        var headers = this.getStandardHeaders();

        var restaurants = this.restTemplate.exchange(
                "/api/client/restaurant",
                HttpMethod.GET, new HttpEntity<>(headers),
                GetRestaurantDto[].class);

        Assertions.assertThat(
            restaurants.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var priceRange = this.restTemplate.exchange(
            "/api/client/restaurant/" + restaurants.getBody()[0].getId() + "/price-range",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            PriceRangeDto.class
        );
        
        Assertions.assertThat(
            priceRange.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            priceRange.getBody()
        ).extracting(PriceRangeDto::getMinimumPrice)
            .isEqualTo(1.25);

        Assertions.assertThat(
            priceRange.getBody()
        ).extracting(PriceRangeDto::getMaximumPrice)
            .isEqualTo(5.4);
    }

    @Test
    void testAddFavoriteRestaurant() {
        var headers = this.getStandardHeaders();

        var restaurants = this.restTemplate.exchange(
                "/api/client/restaurant",
                HttpMethod.GET, new HttpEntity<>(headers),
                GetRestaurantDto[].class);


        var addFavoriteResponse1 = this.restTemplate.exchange(
            "/api/client/restaurant/favorite/" + restaurants.getBody()[0].getId(),
            HttpMethod.POST,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            addFavoriteResponse1.getStatusCode()
        ).isEqualTo(HttpStatus.OK);


        var addFavorite1OnceAgain = this.restTemplate.exchange(
            "/api/client/restaurant/favorite/" + restaurants.getBody()[0].getId(),
            HttpMethod.POST,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            addFavorite1OnceAgain.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);


        var addFavoriteResponse2 = this.restTemplate.exchange(
            "/api/client/restaurant/favorite/" + restaurants.getBody()[1].getId(),
            HttpMethod.POST,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            addFavoriteResponse2.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var getFavoriteRestaurants = this.restTemplate.exchange(
            "/api/client/restaurant/favorite",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetRestaurantDto[].class
        );

        Assertions.assertThat(
            getFavoriteRestaurants.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getFavoriteRestaurants.getBody()
        ).hasSize(2).extracting(GetRestaurantDto::getId).containsOnly(1L, 2L);

        var getFavoriteNumberOne = this.restTemplate.exchange(
            "/api/client/restaurant/favorite/1",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            IsFavoriteDto.class
        );

        Assertions.assertThat(
            getFavoriteNumberOne.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getFavoriteNumberOne.getBody().getFavorite()
        ).isTrue();

        var getFavoriteNotBelongingToList = this.restTemplate.exchange(
            "/api/client/restaurant/favorite/1000",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            IsFavoriteDto.class
        );

        Assertions.assertThat(
            getFavoriteNotBelongingToList.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getFavoriteNotBelongingToList.getBody().getFavorite()
        ).isFalse();

        var removeFavoriteRestaurant = this.restTemplate.exchange(
            "/api/client/restaurant/favorite/" + restaurants.getBody()[0].getId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            removeFavoriteRestaurant.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var removeFavoriteNonFavoritedRestaurant = this.restTemplate.exchange(
            "/api/client/restaurant/favorite/" + 10,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            removeFavoriteNonFavoritedRestaurant.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    void testEatingIntentions() {
        var headers = this.getStandardHeaders();

        var previousIntentions = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientEatIntention[].class
        );

        var restaurants = this.restTemplate.exchange(
            "/api/client/restaurant",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetRestaurantDto[].class
        );

        Assertions.assertThat(
            restaurants.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var assignments = this.restTemplate.exchange(
            "/api/client/restaurant/" + restaurants.getBody()[0].getId() + "/assignment",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto[].class
        );

        Assertions.assertThat(
            assignments.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var intentionDto = new AddEatIntention();
        intentionDto.setAssignmentId(assignments.getBody()[0].getId());
        Set<Long> meals = new HashSet<>();

        meals.add(assignments.getBody()[0].getMenu().getFishMeal().getId());
        meals.add(assignments.getBody()[0].getMenu().getDesertMeal().getId());

        intentionDto.setMealsId(meals);

        var addIntention = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.POST,
            new HttpEntity<>(intentionDto, headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            addIntention.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        intentionDto.getMealsId().add(200L);
        var addIntentionError = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.POST,
            new HttpEntity<>(intentionDto, headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            addIntentionError.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

        intentionDto.getMealsId().remove(200L);
        intentionDto.getMealsId().add(assignments.getBody()[0].getMenu().getMeatMeal().getId());
        var addIntention2 = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.POST,
            new HttpEntity<>(intentionDto, headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            addIntention2.getStatusCode()
        ).isEqualTo(HttpStatus.OK);
        
        var intentions = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientEatIntention[].class
        );

        Assertions.assertThat(
            intentions.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            intentions.getBody()
        ).hasSize(previousIntentions.getBody().length + 2);

        intentionDto.getMealsId().remove(assignments.getBody()[0].getMenu().getMeatMeal().getId());
        var updateIntentions = this.restTemplate.exchange(
            "/api/client/intention/" + addIntention2.getBody().getId(),
            HttpMethod.PUT,
            new HttpEntity<>(intentionDto, headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            updateIntentions.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            updateIntentions.getBody().getMeals()
        ).extracting(GetPutMealDto::getId)
            .containsOnly(
                assignments.getBody()[0].getMenu().getFishMeal().getId(),
                assignments.getBody()[0].getMenu().getDesertMeal().getId()
            );

        var deleteIntention = this.restTemplate.exchange(
            "/api/client/intention/" + addIntention2.getBody().getId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            deleteIntention.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        intentions = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientEatIntention[].class
        );

        Assertions.assertThat(
            intentions.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            intentions.getBody()
        ).hasSize(previousIntentions.getBody().length + 1);

        var getIntention = this.restTemplate.exchange(
            "/api/client/intention/" + addIntention.getBody().getId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            getIntention.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getIntention.getBody()
        ).extracting(GetClientEatIntention::getId)
            .isEqualTo(addIntention.getBody().getId());

    }

    private void registerClient() {
        this.restTemplate.postForEntity("/api/client/register", this.clientUser, RegisterUserResponseDto.class);
    }
    
    private JwtResponse authenticateClient() {
        var jwtRequest = new JwtRequest();
        jwtRequest.setEmail(this.clientUser.getEmail());
        jwtRequest.setPassword(this.clientUser.getPassword());
        
        return this.restTemplate.postForEntity("/api/auth/sign-in", jwtRequest, JwtResponse.class).getBody();
    }
    
    private HttpHeaders getStandardHeaders() {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);
        return headers;
    }
}
