package pt.feup.les.feupfood.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import pt.feup.les.feupfood.dto.AddAssignmentDto;
import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.AddMenuDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
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

	// meal endpoints
	@GetMapping("meal/{id}")
	public ResponseEntity<ResponseInterfaceDto> getMeal(
		Principal user,
		@PathVariable Long id
	) {
		log.info("[meal/id] Requiring meal number: " + id);
		return this.service.getMeal(user, id);
	}

	@GetMapping("meal")
	public ResponseEntity<List<GetPutMealDto>> getMeals(
		Principal user
	) {
		return this.service.getMeals(user);
	}

	@PostMapping("meal")
	public ResponseEntity<ResponseInterfaceDto> addMeal(
		Principal user,
		@RequestBody AddMealDto mealDto
	) {
		return this.service.addMeal(user, mealDto);
	}

	@PutMapping("meal/{id}")
	public ResponseEntity<GetPutMealDto> updateMeal(
		Principal user,
		@RequestBody AddMealDto mealDto,
		@PathVariable Long id
	) {
		return this.service.updateMeal(user, id, mealDto);
	}

	@DeleteMapping("meal/{id}")
	public ResponseEntity<String> deleteMeal(
		Principal user,
		@PathVariable Long id
	) {
		return this.service.deleteMeal(user, id);
	}

	// menu endpoints
	@GetMapping("menu")
	public ResponseEntity<List<GetPutMenuDto>> getMenus(
		Principal user
	) {
		return this.service.getMenus(user);
	}

	@GetMapping("menu/{id}")
	public ResponseEntity<GetPutMenuDto> getMenu(
		Principal user,
		@PathVariable Long id
	) {
		return this.service.getMenu(user, id);
	}

	@PostMapping("menu")
	public ResponseEntity<GetPutMenuDto> addMenu(
		Principal user,
		@RequestBody AddMenuDto menuDto
	) {
		log.info(menuDto);
		return this.service.addMenu(user, menuDto);
	}

	@PutMapping("menu/{id}")
	public ResponseEntity<GetPutMenuDto> updateMenu(
		Principal user,
		@RequestBody AddMenuDto menuDto,
		@PathVariable Long id
	) {
		return this.service.updateMenu(user, menuDto, id);
	}

	@DeleteMapping("menu/{id}")
	public ResponseEntity<String> deleteMenu(
		Principal user,
		@PathVariable Long id
	) {
		return this.service.deleteMenu(user, id);
	}

	// assignment endpoints
	@GetMapping("assignment")
	public ResponseEntity<List<ResponseInterfaceDto>> getAssignments(
		Principal user
	) {
		return this.service.getAssignments(user);
	}
	// TODO assignments for next 7 days

	@PostMapping("assignment")
	public ResponseEntity<ResponseInterfaceDto> addAssignment(
		Principal user,
		@RequestBody AddAssignmentDto assignmentDto
	) {
		return this.service.addAssignment(user, assignmentDto);
	}

}
