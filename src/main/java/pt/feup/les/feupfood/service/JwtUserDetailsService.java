package pt.feup.les.feupfood.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pt.feup.les.feupfood.dto.UserDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService{
    
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

    @Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		var user = this.userRepository.findByEmail(email).orElseThrow(
			() -> new UsernameNotFoundException("User not found with email: " + email)
		);

		return new User(user.getEmail(), user.getPassword(),
				Arrays.asList(
					new SimpleGrantedAuthority("ROLE_" + user.getRole())
				));
	}

	public DAOUser save(UserDto user) {
		// verify if role was provided
		if (user.getRole() == null || user.getRole().equals(""))
			return null;

		// check repeated usernames
		var checkUser = this.userRepository.findByEmail(
			user.getEmail()
		);

		if (checkUser.isPresent())
			throw new AuthenticationServiceException("There is already a user with email: " + user.getEmail());

		var userDAO = new DAOUser();
		userDAO.setFirstName(user.getFirstName());
		userDAO.setLastName(user.getLastName());
		userDAO.setPassword(this.bcryptEncoder.encode(
			user.getPassword()
		));
		userDAO.setEmail(user.getEmail());
		userDAO.setRole(user.getRole());

		return this.userRepository.save(userDAO);
	}
}
