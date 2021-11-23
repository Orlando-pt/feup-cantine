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

import pt.feup.les.feupfood.dto.JwtRequest;
import pt.feup.les.feupfood.dto.JwtResponse;
import pt.feup.les.feupfood.dto.UserDto;
import pt.feup.les.feupfood.model.DAOUser;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestaurantController_RestTemplateIT {

    @LocalServerPort
    int RANDOM_PORT;

    @Autowired
    private TestRestTemplate restTemplate;

private UserDto restaurantUser;
    private String token;

    public RestaurantController_RestTemplateIT() {
        this.restaurantUser = new UserDto();

        this.restaurantUser.setEmail("restaurant@mail.com");
        this.restaurantUser.setPassword("secretPassword");
    }

    @BeforeAll
    public void setup() {
        this.registerRestaurant();
        this.token = this.authenticateRestaurant().getJwttoken();
    }

    @Test
    void callRestaurantHome() {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);

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

    private void registerRestaurant() {
        this.restTemplate.postForEntity("/api/restaurant/register",
                this.restaurantUser,
                DAOUser.class);
    }

    private JwtResponse authenticateRestaurant() {
        var jwtRequest = new JwtRequest();
        jwtRequest.setEmail(this.restaurantUser.getEmail());
        jwtRequest.setPassword(this.restaurantUser.getPassword());

        return this.restTemplate.postForEntity(
            "/api/restaurant/authenticate",
            jwtRequest,
            JwtResponse.class
        ).getBody();
    }
}