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

    @GetMapping("restaurant/{id}/assignment")
    public ResponseEntity<List<GetAssignmentDto>> getRestaurantAssignments(@PathVariable Long id) {
        return this.clientService.getAssignmentsOfRestaurant(id);
    }

    @GetMapping("restaurant/{id}/assignment/{n}")
    public ResponseEntity<List<GetAssignmentDto>> getRestaurantAssignmentsForNDays(
        @PathVariable Long id,
        @PathVariable int n
    ) {
        return this.clientService.getAssignmentsOfRestaurantForNDays(id, n);
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

    // client intends to eat a restaurant (endpoints)
    @GetMapping("intention")
    public ResponseEntity<List<GetClientEatIntention>> getEatingIntentions(
        Principal user
    ) {
        return this.clientService.getEatIntentions(user);
    }

    @GetMapping("intention/{id}")
    public ResponseEntity<GetClientEatIntention> getEatingIntention(
        Principal user,
        @PathVariable Long id
    ) {
        return this.clientService.getEatIntention(user, id);
    }

    @PostMapping("intention")
    public ResponseEntity<GetClientEatIntention> addEatingIntention(
        Principal user,
        @RequestBody AddEatIntention intentionDto
    ) {
        return this.clientService.addEatIntention(user, intentionDto);
    }

    @PutMapping("intention/{id}")
    public ResponseEntity<GetClientEatIntention> updateEatingIntention(
        Principal user,
        @PathVariable Long id,
        @RequestBody PutClientEatIntention intentionDto
    ) {
        return this.clientService.updateEatIntention(user, id, intentionDto);
    }

    @DeleteMapping("intention/{id}")
    public ResponseEntity<String> deleteEatingIntention(
        Principal user,
        @PathVariable Long id
    ) {
        return this.clientService.removeEatIntention(user, id);
    }

}
