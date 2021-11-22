package pt.feup.les.feupfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import pt.feup.les.feupfood.dto.JwtRequest;
import pt.feup.les.feupfood.dto.UserDto;

@RestController
@CrossOrigin
@RequestMapping("/admin/")
@Log4j2
public class AdminController {

    @Autowired
    private JwtAuthenticationControllerUtil jwtAuthenticationUtil;
    
    @PostMapping("authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		log.info("Authenticating: " + authenticationRequest);

		return this.jwtAuthenticationUtil.createAuthenticationToken(authenticationRequest);
	}

	@PostMapping("register")
	public ResponseEntity<?> saveUser(@RequestBody UserDto userDto) throws Exception {
		log.info("Saving new user: " + userDto);

        userDto.setRole("ADMIN");
		return this.jwtAuthenticationUtil.saveUser(userDto);
	}

	@GetMapping("home")
	public ResponseEntity<String> home() {
		return ResponseEntity.ok("Hello admin!");
	}
}
