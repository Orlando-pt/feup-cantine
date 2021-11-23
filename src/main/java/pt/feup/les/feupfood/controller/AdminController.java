package pt.feup.les.feupfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import pt.feup.les.feupfood.dto.JwtRequest;
import pt.feup.les.feupfood.dto.JwtResponse;
import pt.feup.les.feupfood.dto.UserDto;
import pt.feup.les.feupfood.model.DAOUser;

@RestController
// @CrossOrigin(originPatterns = "http://localhost:[*]")
@CrossOrigin
@RequestMapping("/api/admin/")
@Log4j2
public class AdminController {

    @Autowired
    private JwtAuthenticationControllerUtil jwtAuthenticationUtil;
    
    @PostMapping("authenticate")
	public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws UsernameNotFoundException, DisabledException, BadCredentialsException{
		log.info("Authenticating: " + authenticationRequest);

		return this.jwtAuthenticationUtil.createAuthenticationToken(authenticationRequest);
	}

	@PostMapping("register")
	public ResponseEntity<DAOUser> saveUser(@RequestBody UserDto userDto) throws AuthenticationServiceException {
		log.info("Saving new user: " + userDto);

        userDto.setRole("ADMIN");
		return this.jwtAuthenticationUtil.saveUser(userDto);
	}

	@GetMapping("home")
	public ResponseEntity<String> home() {
		return ResponseEntity.ok("Hello admin!");
	}
}
