package pt.feup.les.feupfood.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.RegisterUserDto;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.service.RestaurantService;

@RestController
@CrossOrigin
@RequestMapping("/api/restaurant/")
@Log4j2
public class RestaurantController {
    
    @Autowired
    private JwtAuthenticationControllerUtil jwtAuthenticationUtil;
    
	@Autowired
	private RestaurantService service;

	@PostMapping("register")
	public ResponseEntity<RegisterUserResponseDto> saveUser(@RequestBody RegisterUserDto userDto) throws AuthenticationServiceException {
		log.info("Saving new user: " + userDto);

		return this.jwtAuthenticationUtil.saveUser(userDto, "ROLE_USER_RESTAURANT");
	}

	@GetMapping("home")
	public ResponseEntity<String> home() {
		return ResponseEntity.ok("Hello restaurant owner!");
	}

	@GetMapping("profile")
	public ResponseEntity<RestaurantProfileDto> getRestaurantProfile(
		Principal user
	) {
		return this.service.getRestaurantProfile(user);
	}

	@PutMapping("profile")
	public ResponseEntity<ResponseInterfaceDto> updateRestaurantProfile(
		@RequestBody RestaurantProfileDto profileDto,
		Principal user
	) {
		return this.service.updateRestaurantProfile(user, profileDto);
	}

	@GetMapping("meal/{id}")
	public ResponseEntity<ResponseInterfaceDto> getMeal(
		Principal user,
		@PathVariable Long id
	) {
		log.info("[meal/id] Requiring meal number: " + id);
		return this.service.getMeal(user, id);
	}

	@PostMapping("meal")
	public ResponseEntity<ResponseInterfaceDto> addMeal(
		Principal user,
		@RequestBody AddMealDto mealDto
	) {
		return this.service.addMeal(user, mealDto);
	}
}
