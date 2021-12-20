package pt.feup.les.feupfood.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
                GetRestaurantDto[].class);

        Assertions.assertThat(getReviewsByRestaurantId.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(getReviewsByRestaurantId.getBody()).hasSize(2).contains(getReviewsByRestaurantId.getBody());
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
