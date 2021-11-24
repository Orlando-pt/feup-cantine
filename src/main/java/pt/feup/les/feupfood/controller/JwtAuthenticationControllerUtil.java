package pt.feup.les.feupfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import pt.feup.les.feupfood.dto.UserDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.service.JwtUserDetailsService;

@Service
public class JwtAuthenticationControllerUtil {

	@Autowired
	private JwtUserDetailsService userDetailsService;
	
	public ResponseEntity<DAOUser> saveUser(UserDto userDto) throws AuthenticationServiceException {
		var response = this.userDetailsService.save(userDto);
		response.setPassword("");
		return ResponseEntity.ok(response);
	}
}
