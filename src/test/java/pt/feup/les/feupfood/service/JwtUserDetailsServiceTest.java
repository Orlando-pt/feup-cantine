package pt.feup.les.feupfood.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import pt.feup.les.feupfood.dto.RegisterUserResponseDto;
import pt.feup.les.feupfood.dto.UserDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.repository.RedisSessionRepository;
import pt.feup.les.feupfood.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class JwtUserDetailsServiceTest {
   
    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisSessionRepository redisSessionRepository;

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    private DAOUser daoUser;

    public JwtUserDetailsServiceTest() {
        this.daoUser = new DAOUser();
        daoUser.setFullName("Diogo");
        daoUser.setEmail("diogo@mail.com");
        daoUser.setPassword("Secret");
        daoUser.setRole("ADMIN");
    }

    /**
     * test when no user is found
     */
    @Test
    void onLoadNoUserWasFoundTest() {
        var email = "Candido@mail.com";
        Mockito.when(
            this.userRepository.findByEmail(email)
        ).thenReturn(
            Optional.ofNullable(null)
        );

        Assertions.assertThatThrownBy(
            () -> this.jwtUserDetailsService.loadUserByUsername(email)
        ).isInstanceOf(UsernameNotFoundException.class);

        Mockito.verify(
            this.userRepository,
            Mockito.times(1)
        ).findByEmail(email);
    }

    /**
     * check when the user is correctly loaded
     */
    @Test
    void onLoadUserReturnUser() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(this.daoUser.getRole()));

        var expectedUserReturn = new User(
            this.daoUser.getEmail(),
            this.daoUser.getPassword(),
            roles
        );

        Mockito.when(
            this.userRepository.findByEmail(this.daoUser.getEmail())
        ).thenReturn(
            Optional.of(this.daoUser)
        );

        Assertions.assertThat(
            this.jwtUserDetailsService.loadUserByUsername(
                this.daoUser.getEmail())
        ).isEqualTo(expectedUserReturn);

        Mockito.verify(this.userRepository,
            Mockito.times(1)).findByEmail(this.daoUser.getEmail());
    }

    /**
     * trying to save a username that already exists
     */
    @Test
    void savingUserThatAlreadyExistsShouldThrowRuntimeException() {
        Mockito.when(
            this.userRepository.findByEmail(this.daoUser.getEmail())
        ).thenReturn(
            Optional.of(this.daoUser)
        );

        var userDto = new UserDto();
        userDto.setEmail(this.daoUser.getEmail());
        userDto.setRole("ADMIN");

        Assertions.assertThatThrownBy(
            () -> this.jwtUserDetailsService.save(
                userDto
            )
        ).isInstanceOf(AuthenticationServiceException.class);

        Mockito.verify(
            this.userRepository, Mockito.times(1)
        ).findByEmail(userDto.getEmail());
    }

    /**
     * registering successfully a user on the database
     */
    @Test
    void registerSuccessfullyUser() {
        var userDto = new UserDto();
        userDto.setEmail("Franciso@mail.com");
        userDto.setPassword("passwordSecret");
        userDto.setRole("ADMIN");

        Mockito.when(
            this.userRepository.findByEmail(userDto.getEmail())
        ).thenReturn(
            Optional.ofNullable(null)
        );

        Mockito.when(
            this.encoder.encode(userDto.getPassword())
        ).thenReturn(userDto.getPassword());

        var expectedDAOUser = new DAOUser();
        expectedDAOUser.setEmail(userDto.getEmail());
        expectedDAOUser.setPassword(userDto.getPassword());
        expectedDAOUser.setRole(userDto.getRole());

        var expectedResponseDto = new RegisterUserResponseDto();
        expectedResponseDto.setEmail(userDto.getEmail());
        expectedResponseDto.setRole(
            userDto.getRole()
        );
        
        Mockito.when(
            this.userRepository.save(expectedDAOUser)
        ).thenReturn(expectedDAOUser);

        Assertions.assertThat(
            this.jwtUserDetailsService.save(userDto)
        ).isEqualTo(expectedResponseDto);

        Mockito.verify(
            this.userRepository,
            Mockito.times(1)
        ).findByEmail(userDto.getEmail());

        Mockito.verify(
            this.encoder,
            Mockito.times(1)
        ).encode(userDto.getPassword());

        Mockito.verify(
            this.userRepository,
            Mockito.times(1)
        ).save(expectedDAOUser);
    }

    /**
     * check behaviour of empty role
     */
    @Test
    void onEmptyRoleThenReturnNull() {
        var userDto = new UserDto();
        userDto.setEmail("something@mail.com");
        
        Assertions.assertThat(
            this.jwtUserDetailsService.save(userDto)
        ).isNull();

        // now lets verify with a empty string
        userDto.setRole("");

        Assertions.assertThat(
            this.jwtUserDetailsService.save(userDto)
        ).isNull();
    }

    /**
     * test user is active
     */
    @Test
    void whenUserIsOnCacheThenUserIsActiveShouldReturnTrueAndFalseOtherwise() {
        var user = "pinkpanter@mail.com";
        Mockito.when(
            this.redisSessionRepository.userIsActive(
                user
            )
        ).thenReturn("active");

        Assertions.assertThat(
            this.jwtUserDetailsService.userIsActive(user)
        ).isTrue();

        // now if it is not stored on cache
        Mockito.when(
            this.redisSessionRepository.userIsActive(user)
        ).thenReturn(null);

        Assertions.assertThat(
            this.jwtUserDetailsService.userIsActive(user)
        ).isFalse();
    }
}
