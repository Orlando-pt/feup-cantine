package pt.feup.les.feupfood.controller;

import lombok.extern.log4j.Log4j2;

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

    // register endpoints
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

    @GetMapping("review/restaurant/{id}")
    public ResponseEntity<List<GetPutClientReviewDto>> getClientReviewsByRestaurantId(@PathVariable Long id) {

        return this.clientService.getReviewsFromRestaurantByRestaurantId(id);
    }

    @PostMapping("review/restaurant/{id}")
    public ResponseEntity<ResponseInterfaceDto> saveClientReviewsByRestaurantId(
            @PathVariable Long id,
            @RequestBody AddClientReviewDto clientReviewDto,
            Principal user
    ) {

        return this.clientService.saveReviewsFromRestaurantByRestaurantId(id, clientReviewDto, user);

    }

    // restaurant endpoints
    @GetMapping("restaurant/{id}/price-range")
    public ResponseEntity<PriceRangeDto> getPriceRangeOfRestaurant(
        @PathVariable Long id
    ) {
        return this.clientService.getPriceRangeOfRestaurant(id);
    }

    @GetMapping("restaurant")
    public ResponseEntity<List<GetRestaurantDto>> getClientReviews() {

        return this.clientService.getAllRestaurants();
    }

    @GetMapping("restaurant/{id}")
    public ResponseEntity<ResponseInterfaceDto> getRestaurant(@PathVariable Long id) {
        log.info("[restaurant/id] Requiring restaurant id: " + id);
        return this.clientService.getRestaurantById(id);
    }

    @GetMapping("home")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello client!");
    }

    // add favorite restaurant endpoints
    @GetMapping("restaurant/favorite")
    public ResponseEntity<List<GetRestaurantDto>> getClientFavoriteRestaurants(
        Principal user
    ) {
        return this.clientService.getFavoriteRestaurants(user);
    }

    @GetMapping("restaurant/favorite/{id}")
    public ResponseEntity<IsFavoriteDto> restaurantIsOnFavoriteList(
        Principal user,
        @PathVariable Long id
    ) {
        return this.clientService.restaurantIsFavorite(user, id);
    }

    @PostMapping("restaurant/favorite/{id}")
    public ResponseEntity<String> addClientFavoriteRestaurant(
        Principal user,
        @PathVariable Long id
    ) {
        return this.clientService.addFavoriteRestaurant(user, id);
    }

    @DeleteMapping("restaurant/favorite/{id}")
    public ResponseEntity<String> removeClientFavoriteRestaurant(
        Principal user,
        @PathVariable Long id
    ) {
        return this.clientService.removeFavoriteRestaurant(user, id);
    }

}
