package pt.feup.les.feupfood.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
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
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO register using db
		var user = this.userRepository.findByUsername(username).orElseThrow(
			() -> new UsernameNotFoundException("User not found with username: " + username)
		);

		return new User(user.getUsername(), user.getPassword(),
				new ArrayList<>());
	}

	public DAOUser save(UserDto user) {
		var userDAO = new DAOUser();
		userDAO.setUsername(user.getUsername());
		userDAO.setPassword(this.bcryptEncoder.encode(
			user.getPassword()
		));

		return this.userRepository.save(userDAO);
	}
}
