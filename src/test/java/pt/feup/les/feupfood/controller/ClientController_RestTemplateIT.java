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
import pt.feup.les.feupfood.dto.RegisterUserDto;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;

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

        var response = this.restTemplate.exchange(
            "/api/client/home",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            response.getBody()
        ).isEqualTo("Hello client!");
    }

    private void registerClient() {
        this.restTemplate.postForEntity("/api/client/register",
                this.clientUser,
                RegisterUserResponseDto.class);
    }

    private JwtResponse authenticateClient() {
        var jwtRequest = new JwtRequest();
        jwtRequest.setEmail(this.clientUser.getEmail());
        jwtRequest.setPassword(this.clientUser.getPassword());

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
