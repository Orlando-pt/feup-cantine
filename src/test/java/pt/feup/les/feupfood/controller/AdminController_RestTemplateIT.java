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
public class AdminController_RestTemplateIT {
    
    @LocalServerPort
    int RANDOM_PORT;

    @Autowired
    private TestRestTemplate restTemplate;

    private UserDto adminUser;
    private String token;

    public AdminController_RestTemplateIT() {
        this.adminUser = new UserDto();

        this.adminUser.setEmail("admin@mail.com");
        this.adminUser.setPassword("secretPassword");
    }

    @BeforeAll
    public void setup() {
        this.registerAdmin();
        this.token = this.authenticateAdmin().getJwttoken();
    }

    @Test
    void callAdminHome() {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);

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
                DAOUser.class);
    }

    private JwtResponse authenticateAdmin() {
        var jwtRequest = new JwtRequest();
        jwtRequest.setEmail(this.adminUser.getEmail());
        jwtRequest.setPassword(this.adminUser.getPassword());

        return this.restTemplate.postForEntity(
            "/api/admin/authenticate",
            jwtRequest,
            JwtResponse.class
        ).getBody();
    }
}
