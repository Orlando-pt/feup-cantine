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
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.ScheduleEnum;
import pt.feup.les.feupfood.repository.AssignMenuRepository;
import pt.feup.les.feupfood.repository.EatIntentionRepository;
import pt.feup.les.feupfood.repository.MenuRepository;
import pt.feup.les.feupfood.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientController_RestTemplateIT {
    
    @LocalServerPort
    int RANDOM_PORT;
    
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignMenuRepository assignMenuRepository;

    @Autowired
    private EatIntentionRepository eatIntentionRepository;
    
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

        var addReview = this.restTemplate.exchange("/api/client/review/restaurant/" + getRestaurantId.getBody()[0].getId(), HttpMethod.POST, new HttpEntity<>(reviewDto, headers), GetClientReviewDto.class);

        var addReview2 = this.restTemplate.exchange("/api/client/review/restaurant/" + getRestaurantId.getBody()[0].getId(),
                HttpMethod.POST, new HttpEntity<>(reviewDto, headers), GetClientReviewDto.class);

        Assertions.assertThat(addReview.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(addReview2.getStatusCode()).isEqualTo(HttpStatus.OK);

        var getReviews = this.restTemplate.exchange("/api/client/review", HttpMethod.GET, new HttpEntity<>(headers), GetClientReviewDto[].class);
        
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
                GetClientReviewDto[].class);
        
        GetClientReviewDto expectedReview = new GetClientReviewDto();
        expectedReview.setComment("Lamentável! Hoje o prato era arroz com molho de tomate.");

        Assertions.assertThat(getReviewsByRestaurantId.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getReviewsByRestaurantId.getBody()
        ).extracting(GetClientReviewDto::getComment)
            .contains(expectedReview.getComment());
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
            .isEqualTo(2.75);

        Assertions.assertThat(
            priceRange.getBody()
        ).extracting(PriceRangeDto::getMaximumPrice)
            .isEqualTo(4.0);
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

        var getAssignmentsForNextDays = this.restTemplate.exchange(
            "/api/client/restaurant/1/assignment/4",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto[].class
        );

        Assertions.assertThat(
            getAssignmentsForNextDays.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var getCurrentAssignment = this.restTemplate.exchange(
            "/api/client/restaurant/1/assignment/now",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto.class
        );

        Assertions.assertThat(
            getCurrentAssignment.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

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

        intentionDto.setAssignmentId(assignments.getBody()[7].getId());
        meals.clear();
        meals.add(assignments.getBody()[7].getMenu().getMeatMeal().getId());
        meals.add(assignments.getBody()[7].getMenu().getFishMeal().getId());
        meals.add(assignments.getBody()[7].getMenu().getDesertMeal().getId());
        intentionDto.setMealsId(meals);
        var addIntention2 = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.POST,
            new HttpEntity<>(intentionDto, headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            addIntention2.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var addDuplicatedIntention2 = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.POST,
            new HttpEntity<>(intentionDto, headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            addDuplicatedIntention2.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);
        
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

        intentionDto.getMealsId().remove(assignments.getBody()[7].getMenu().getMeatMeal().getId());
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
                assignments.getBody()[7].getMenu().getFishMeal().getId(),
                assignments.getBody()[7].getMenu().getDesertMeal().getId()
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

        // test when client tries to add intention after the allowed time
        
        var intentionDtoError = new AddEatIntention();
        intentionDtoError.setAssignmentId(23L);
        intentionDtoError.setMealsId(intentionDto.getMealsId());

        var addIntentionAddError = this.restTemplate.exchange(
            "/api/client/intention",
            HttpMethod.POST,
            new HttpEntity<>(intentionDtoError, headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            addIntentionAddError.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

        // test client stats
        AssignMenu assignment1 = this.assignMenuRepository.findById(28L).orElseThrow();
        AssignMenu assignment2 = this.assignMenuRepository.findById(29L).orElseThrow();
        AssignMenu assignment19 = this.assignMenuRepository.findById(36L).orElseThrow();

        DAOUser client = this.userRepository.findByEmail(this.clientUser.getEmail()).orElseThrow();

        EatIntention previousEatIntention = new EatIntention();
        previousEatIntention.setAssignment(assignment1);
        previousEatIntention.setClient(client);
        previousEatIntention.setCode("987654321");
        previousEatIntention.setValidatedCode(true);
        previousEatIntention.setMeals(Set.of(assignment1.getMenu().getMeals().get(0)));

        EatIntention previousEatIntention2 = new EatIntention();
        previousEatIntention2.setAssignment(assignment2);
        previousEatIntention2.setClient(client);
        previousEatIntention2.setCode("987654333");
        previousEatIntention2.setValidatedCode(false);
        previousEatIntention2.setMeals(Set.of(assignment2.getMenu().getMeals().get(0)));

        this.eatIntentionRepository.save(previousEatIntention);
        this.eatIntentionRepository.save(previousEatIntention2);

        var getStatusOnMoneySaved = this.restTemplate.exchange(
            "/api/client/stats/money-saved",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ClientStats.class
        );

        Assertions.assertThat(
            getStatusOnMoneySaved.getStatusCode()
        ).isEqualTo(HttpStatus.OK); 

        Assertions.assertThat(
            getStatusOnMoneySaved.getBody()
        ).extracting(ClientStats::getIntentionsGiven)
            .isEqualTo(3);

        var getNextIntention = this.restTemplate.exchange(
            "/api/client/intention/next",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            getNextIntention.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getNextIntention.getBody().getAssignment().getSchedule()
        ).isEqualTo(ScheduleEnum.DINNER);

        EatIntention intentionForAFewDays = new EatIntention();
        intentionForAFewDays.setAssignment(assignment19);
        intentionForAFewDays.setClient(client);
        intentionForAFewDays.setCode("989898988");
        intentionForAFewDays.setValidatedCode(false);
        intentionForAFewDays.setMeals(Set.of(assignment19.getMenu().getMeals().get(0)));

        this.eatIntentionRepository.save(intentionForAFewDays);

        getNextIntention = this.restTemplate.exchange(
            "/api/client/intention/next",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientEatIntention.class
        );

        Assertions.assertThat(
            getNextIntention.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getNextIntention.getBody().getAssignment().getSchedule()
        ).isEqualTo(ScheduleEnum.DINNER);

        var getIntentionsToCome = this.restTemplate.exchange(
            "/api/client/intention/from-today",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientEatIntention[].class
        );

        Assertions.assertThat(
            getIntentionsToCome.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getIntentionsToCome.getBody()
        ).hasSize(4);
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
