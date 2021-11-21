package pt.feup.les.feupfood.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import pt.feup.les.feupfood.dto.UserDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class JwtUserDetailsServiceTest {
   
    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    private DAOUser daoUser;

    public JwtUserDetailsServiceTest() {
        this.daoUser = new DAOUser();
        daoUser.setUsername("Diogo");
        daoUser.setPassword("Secret");
        daoUser.setRole("ADMIN");
    }

    /**
     * test when no user is found
     */
    @Test
    void onLoadNoUserWasFoundTest() {
        var username = "Candido";
        Mockito.when(
            this.userRepository.findByUsername(username)
        ).thenReturn(
            Optional.ofNullable(null)
        );

        Assertions.assertThatThrownBy(
            () -> this.jwtUserDetailsService.loadUserByUsername(username)
        ).isInstanceOf(UsernameNotFoundException.class);

        Mockito.verify(
            this.userRepository,
            Mockito.times(1)
        ).findByUsername(username);
    }

    /**
     * check when the user is correctly loaded
     */
    @Test
    void onLoadUserReturnUser() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(this.daoUser.getRole()));

        var expectedUserReturn = new User(
            this.daoUser.getUsername(),
            this.daoUser.getPassword(),
            roles
        );

        Mockito.when(
            this.userRepository.findByUsername(this.daoUser.getUsername())
        ).thenReturn(
            Optional.of(this.daoUser)
        );

        Assertions.assertThat(
            this.jwtUserDetailsService.loadUserByUsername(
                this.daoUser.getUsername()
            )
        ).isEqualTo(expectedUserReturn);

        Mockito.verify(this.userRepository,
            Mockito.times(1)).findByUsername(this.daoUser.getUsername());
    }

    /**
     * trying to save a username that already exists
     */
    @Test
    void savingUserThatAlreadyExistsShouldThrowRuntimeException() {
        Mockito.when(
            this.userRepository.findByUsername(this.daoUser.getUsername())
        ).thenReturn(
            Optional.of(this.daoUser)
        );

        var userDto = new UserDto();
        userDto.setUsername(this.daoUser.getUsername());

        Assertions.assertThatThrownBy(
            () -> this.jwtUserDetailsService.save(
                userDto
            )
        ).isInstanceOf(RuntimeException.class);

        Mockito.verify(
            this.userRepository, Mockito.times(1)
        ).findByUsername(this.daoUser.getUsername());
    }

    /**
     * registering successfully a user on the database
     */
    @Test
    void registerSuccessfullyUser() {
        var userDto = new UserDto();
        userDto.setUsername("Franciso");
        userDto.setPassword("passwordSecret");

        Mockito.when(
            this.userRepository.findByUsername(userDto.getUsername())
        ).thenReturn(
            Optional.ofNullable(null)
        );

        Mockito.when(
            this.encoder.encode(userDto.getPassword())
        ).thenReturn(userDto.getPassword());

        var expectedDAOUser = new DAOUser();
        expectedDAOUser.setUsername(userDto.getUsername());
        expectedDAOUser.setPassword(userDto.getPassword());
        
        Mockito.when(
            this.userRepository.save(expectedDAOUser)
        ).thenReturn(expectedDAOUser);

        Assertions.assertThat(
            this.jwtUserDetailsService.save(userDto)
        ).isEqualTo(expectedDAOUser);

        Mockito.verify(
            this.userRepository,
            Mockito.times(1)
        ).findByUsername(userDto.getUsername());

        Mockito.verify(
            this.encoder,
            Mockito.times(1)
        ).encode(userDto.getPassword());

        Mockito.verify(
            this.userRepository,
            Mockito.times(1)
        ).save(expectedDAOUser);
    }
}
