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

import lombok.extern.log4j.Log4j2;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;
import pt.feup.les.feupfood.dto.UserDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.repository.RedisSessionRepository;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.UserRepository;
import pt.feup.les.feupfood.util.UserParser;

@Service
@Log4j2
public class JwtUserDetailsService implements UserDetailsService{
    
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private RedisSessionRepository redisSessionRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

    @Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		var user = this.loadUserFromDb(email);

		// store user on sign in cache
		this.activateUser(email);

		return new User(user.getEmail(), user.getPassword(),
				Arrays.asList(
					new SimpleGrantedAuthority(user.getRole())
				));
	}

	public RegisterUserResponseDto save(UserDto user) {
		// verify if role was provided
		if (user.getRole() == null || user.getRole().equals(""))
			return null;

		// check repeated usernames
		var checkUser = this.userRepository.findByEmail(
			user.getEmail()
		);

		if (checkUser.isPresent())
			throw new AuthenticationServiceException("There is already a user with email: " + user.getEmail());

		var parser = new UserParser();
		var userDAO = parser.registerUsertoDaoUser(user);
		userDAO.setPassword(this.bcryptEncoder.encode(
			user.getPassword()
		));

		userDAO = this.userRepository.save(userDAO);

		// if user is a restaurant, initialize the restaurant table
		if (user.getRole().equals("ROLE_USER_RESTAURANT"))
			this.initializeRestaurantOnDB(userDAO);

		return parser.daoUserToRegisterUserResponse(
			userDAO
		);
	}

	public boolean userIsActive(String email) {
		return this.redisSessionRepository.userIsActive(email) != null;
	}

	public void activateUser(String email) {
		this.redisSessionRepository.addUser(email);
		log.info("User signed in, user email:" + email);
	}

	public void deactivateUser(String email) {
		this.redisSessionRepository.removeUser(email);
		log.info("User signed out, user email:" + email);
	}

	public DAOUser loadUserFromDb(String email) {
		return this.userRepository.findByEmail(email).orElseThrow(
			() -> new UsernameNotFoundException("User not found with email: " + email)
		);

	}

	private void initializeRestaurantOnDB(DAOUser user) {
		Restaurant restaurant = new Restaurant();
		restaurant.setOwner(user);

		user.setRestaurant(restaurant);

		this.restaurantRepository.save(restaurant);
	}
}
