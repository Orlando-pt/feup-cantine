package pt.feup.les.feupfood.controller;

import java.sql.Time;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.JwtRequest;
import pt.feup.les.feupfood.dto.JwtResponse;
import pt.feup.les.feupfood.dto.RegisterUserDto;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.model.MealTypeEnum;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestaurantController_RestTemplateIT {

    @LocalServerPort
    int RANDOM_PORT;

    @Autowired
    private TestRestTemplate restTemplate;

    private RegisterUserDto restaurantUser;
    private String token;

    public RestaurantController_RestTemplateIT() {
        this.restaurantUser = new RegisterUserDto();

        this.restaurantUser.setEmail("restaurant@mail.com");
        this.restaurantUser.setPassword("secretPassword");
        this.restaurantUser.setConfirmPassword("secretPassword");
        this.restaurantUser.setFullName("ajsbfasjbfas");
        this.restaurantUser.setTerms(true);
    }

    @BeforeAll
    public void setup() {
        this.registerRestaurant();
        this.token = this.authenticateRestaurant().getJwttoken();
    }

    @Test
    void callRestaurantHome() {
        var headers = this.getStandardHeaders();

        var response = this.restTemplate.exchange(
            "/api/restaurant/home",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            response.getBody()
        ).isEqualTo("Hello restaurant owner!");
    }

    @Test
    void restaurantProfileTest() {
        var headers = this.getStandardHeaders();

        var response = this.restTemplate.exchange(
            "/api/restaurant/profile",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            RestaurantProfileDto.class
        );

        Assertions.assertThat(
            response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            response.getBody().getLocation()
        ).isNull();

        // update the profile information
        RestaurantProfileDto updateProfileDto = new RestaurantProfileDto();
        updateProfileDto.setFullName(response.getBody().getFullName());
        updateProfileDto.setLocation("I do not really know");
        updateProfileDto.setMorningOpeningSchedule(
            Time.valueOf("1:10:10")
        );

        var httpEntity = new HttpEntity<>(updateProfileDto, headers);
        
        
        var responseAfterUpdate = this.restTemplate.exchange(
            "/api/restaurant/profile",
            HttpMethod.PUT,
            httpEntity,
            RestaurantProfileDto.class
        );

        Assertions.assertThat(
            responseAfterUpdate.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        // check that the information was updated
        var getResponseAfterUpdate = this.restTemplate.exchange(
            "/api/restaurant/profile",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            RestaurantProfileDto.class
        );

        Assertions.assertThat(
            getResponseAfterUpdate.getBody().getLocation()
        ).isEqualTo(updateProfileDto.getLocation());

    }

    @Test
    void addAndGetMealTest() {
        var headers = this.getStandardHeaders();

        var mealDto = new AddMealDto();
        mealDto.setDescription("Spagheti with atum");
        mealDto.setMealType(MealTypeEnum.FISH);
        mealDto.setNutritionalInformation("Tuna is very healthy.");

        var mealDto1 = new AddMealDto();
        mealDto1.setDescription("Rice with atum and brocoli");
        mealDto1.setMealType(MealTypeEnum.FISH);
        mealDto1.setNutritionalInformation("Tuna is very healthy.");

        var mealDto2 = new AddMealDto();
        mealDto2.setDescription("Francesinha");
        mealDto2.setMealType(MealTypeEnum.MEAT);
        mealDto2.setNutritionalInformation("Francesinha is not that healthy, but it heals the soul.");

        var responseMeal1 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto, headers),
            GetPutMealDto.class
        );

        Assertions.assertThat(
            responseMeal1.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var responseMeal2 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto1, headers),
            GetPutMealDto.class
        );

        var responseMeal3 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto2, headers),
            GetPutMealDto.class
        );

        var getMeals = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMealDto[].class
        );

        Assertions.assertThat(
            getMeals.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getMeals.getBody()
        ).hasSize(3).contains(responseMeal1.getBody(), responseMeal2.getBody(), responseMeal3.getBody());

        var getMeal = this.restTemplate.exchange(
            "/api/restaurant/meal/" + responseMeal1.getBody().getId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMealDto.class
        );

        Assertions.assertThat(
            getMeal.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getMeal.getBody().getId()
        ).isEqualTo(responseMeal1.getBody().getId());

        var deleteMeal = this.restTemplate.exchange(
            "/api/restaurant/meal/" + responseMeal1.getBody().getId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            deleteMeal.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var getMealsAfterDelete = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMealDto[].class
        );

        Assertions.assertThat(
            getMealsAfterDelete.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getMealsAfterDelete.getBody()
        ).hasSize(2).contains(responseMeal2.getBody(), responseMeal3.getBody())
                    .doesNotContain(responseMeal1.getBody());
    }

    private void registerRestaurant() {
        this.restTemplate.postForEntity("/api/restaurant/register",
                this.restaurantUser,
                RegisterUserResponseDto.class);
    }

    private JwtResponse authenticateRestaurant() {
        var jwtRequest = new JwtRequest();
        jwtRequest.setEmail(this.restaurantUser.getEmail());
        jwtRequest.setPassword(this.restaurantUser.getPassword());

        return this.restTemplate.postForEntity(
            "/api/auth/sign-in",
            jwtRequest,
            JwtResponse.class
        ).getBody();
    }
    
    private HttpHeaders getStandardHeaders() {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);
        return headers;
    }
}