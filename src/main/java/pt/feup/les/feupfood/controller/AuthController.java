package pt.feup.les.feupfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import pt.feup.les.feupfood.config.JwtTokenUtil;
import pt.feup.les.feupfood.dto.JwtRequest;
import pt.feup.les.feupfood.dto.JwtResponse;
import pt.feup.les.feupfood.service.JwtUserDetailsService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@CrossOrigin
@RequestMapping("/api/auth/")
@Log4j2
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("sign-in")
    public ResponseEntity<JwtResponse> signin(@RequestBody JwtRequest jwtRequest) throws UsernameNotFoundException, DisabledException, BadCredentialsException{
        log.info("Authenticating user with email: " + jwtRequest.getEmail());
        this.authenticate(jwtRequest.getEmail(), jwtRequest.getPassword());

        final UserDetails userDetails = this.userDetailsService.loadUserByUsername(
            jwtRequest.getEmail()
        );

        final String token = this.jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token, userDetails.getAuthorities().iterator().next().getAuthority()));
    }
    
    private void authenticate(String username, String password) throws DisabledException, BadCredentialsException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new DisabledException("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("INVALID_CREDENTIALS", e);
		}
	}
}
