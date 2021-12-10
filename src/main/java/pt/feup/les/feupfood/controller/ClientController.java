package pt.feup.les.feupfood.controller;

import lombok.extern.log4j.Log4j2;

import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;
import pt.feup.les.feupfood.dto.*;
import pt.feup.les.feupfood.service.ClientService;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/client/")
@Log4j2
public class ClientController {

    @Autowired
    private JwtAuthenticationControllerUtil jwtAuthenticationUtil;

    @Autowired
    private ClientService clientService;

    @PostMapping("register")
    public ResponseEntity<RegisterUserResponseDto> saveUser(@RequestBody RegisterUserDto userDto) throws AuthenticationServiceException {
        log.info("Saving new user: " + userDto);

        return this.jwtAuthenticationUtil.saveUser(userDto, "ROLE_USER_CLIENT");
    }

    // update endpoints
    @GetMapping("profile")
    public ResponseEntity<UpdateProfileDto> getProfile(
        Principal user
    ) {
        return this.clientService.getProfile(user);
    }

    @PutMapping("profile")
    public ResponseEntity<UpdateProfileDto> updateProfile(
        Principal user,
        @RequestBody UpdateProfileDto profileDto
    ) {
        return this.clientService.updateProfile(user, profileDto);
    }

    // review endpoints
    @GetMapping("review")
    public ResponseEntity<List<GetPutClientReviewDto>> getClientReviews(Principal user) {

        return this.clientService.getUserReviewsFromClient(user);
    }

    @PostMapping("review")
    public ResponseEntity<ResponseInterfaceDto> saveReview(@RequestBody AddClientReviewDto clientReviewDto, Principal user) {

        return this.clientService.saveReview(user, clientReviewDto);
    }

    // restaurant endpoints
    @GetMapping("restaurant")
    public ResponseEntity<List<GetRestaurantDto>> getClientReviews() {

        return this.clientService.getAllRestaurants();
    }


    @GetMapping("home")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello client!");
    }
}
