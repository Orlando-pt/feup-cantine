package pt.feup.les.feupfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import pt.feup.les.feupfood.dto.RegisterUserDto;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;
import pt.feup.les.feupfood.service.JwtUserDetailsService;
import pt.feup.les.feupfood.util.UserParser;

@Service
public class JwtAuthenticationControllerUtil {

	@Autowired
	private JwtUserDetailsService userDetailsService;
	
	public ResponseEntity<RegisterUserResponseDto> saveUser(RegisterUserDto userDto, String role) throws AuthenticationServiceException, BadCredentialsException {
		// confirm passwords
		if ("".equals(userDto.getPassword()) || !userDto.getPassword().equals(userDto.getConfirmPassword()))
			return ResponseEntity.badRequest().build();

		var parser = new UserParser();
		var response = this.userDetailsService.save(parser.registerUsertoUserDto(userDto, role));
		return ResponseEntity.ok(response);
	}
}
