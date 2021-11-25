package pt.feup.les.feupfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import pt.feup.les.feupfood.dto.RegisterUserDto;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;

@RestController
@CrossOrigin
@RequestMapping("/api/restaurant/")
@Log4j2
public class RestaurantController {
    
    @Autowired
    private JwtAuthenticationControllerUtil jwtAuthenticationUtil;
    
	@PostMapping("register")
	public ResponseEntity<RegisterUserResponseDto> saveUser(@RequestBody RegisterUserDto userDto) throws AuthenticationServiceException {
		log.info("Saving new user: " + userDto);

		return this.jwtAuthenticationUtil.saveUser(userDto, "ROLE_USER_RESTAURANT");
	}

	@GetMapping("home")
	public ResponseEntity<String> home() {
		return ResponseEntity.ok("Hello restaurant owner!");
	}
}
