package pt.feup.les.feupfood.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
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
public class AdminController_RestTemplateIT {
    
    @LocalServerPort
    int RANDOM_PORT;

    @Autowired
    private TestRestTemplate restTemplate;

    private RegisterUserDto adminUser;
    private String token;

    public AdminController_RestTemplateIT() {
        this.adminUser = new RegisterUserDto();

        this.adminUser.setEmail("admin@mail.com");
        this.adminUser.setPassword("secretPassword");
        this.adminUser.setConfirmPassword("secretPassword");
        this.adminUser.setFullName("António");
        this.adminUser.setTerms(true);
    }

    @BeforeAll
    public void setup() {
        this.registerAdmin();
        this.token = this.authenticateAdmin().getJwttoken();
    }

    // @AfterAll
    // public void teardown() {
    //     this.signOutAdmin();
    // }

    @Test
    void callAdminHome() {
        var headers = this.getStandardHeaders();

        var response = this.restTemplate.exchange(
            "/api/admin/home",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            response.getBody()
        ).isEqualTo("Hello admin!");
    }

    private void registerAdmin() {
        this.restTemplate.postForEntity("/api/admin/register",
                this.adminUser,
                RegisterUserResponseDto.class);
    }

    private JwtResponse authenticateAdmin() {
        var jwtRequest = new JwtRequest();
        jwtRequest.setEmail(this.adminUser.getEmail());
        jwtRequest.setPassword(this.adminUser.getPassword());

        return this.restTemplate.postForEntity(
            "/api/auth/sign-in",
            jwtRequest,
            JwtResponse.class
        ).getBody();
    }

    private void signOutAdmin() {
        var headers = this.getStandardHeaders();

        var response = this.restTemplate.exchange(
            "/api/auth/sign-out",
            HttpMethod.POST,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            response.getStatusCode()
        ).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private HttpHeaders getStandardHeaders() {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);
        return headers;
    }
}
