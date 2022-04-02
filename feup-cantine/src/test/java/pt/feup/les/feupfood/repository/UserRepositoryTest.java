package pt.feup.les.feupfood.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;

@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private DAOUser user1;
    private DAOUser user2;
    private DAOUser user3;
    private DAOUser user4;

    private Restaurant restaurant1;
    private Restaurant restaurant2;

    public UserRepositoryTest() {
        this.user1 = new DAOUser();
        this.user1.setFullName("Orlando");
        this.user1.setEmail("orlando@mail.com");
        this.user1.setPassword("SecretPassword");
        this.user1.setRole("ROLE_ADMIN");
        
        this.user2 = new DAOUser();
        this.user2.setFullName("Francisco");
        this.user2.setEmail("francisco@mail.com");
        this.user2.setPassword("AnotherSecretPassword");
        this.user2.setRole("ROLE_USER_CLIENT");

        this.user3 = new DAOUser();
        this.user3.setFullName("Diogo Restaurant");
        this.user3.setEmail("diogoboss@mail.com");
        this.user3.setPassword("AnotherSecretPassword");
        this.user3.setRole("ROLE_USER_RESTAURANT");

        this.user4 = new DAOUser();
        this.user4.setFullName("Restaurant Tres bon");
        this.user4.setEmail("tresbon@mail.com");
        this.user4.setPassword("AnotherSecretPassword");
        this.user4.setRole("ROLE_USER_RESTAURANT");

        this.restaurant1 = new Restaurant();
        this.restaurant2 = new Restaurant();

        this.restaurant1.setOwner(this.user3);
        this.restaurant2.setOwner(this.user4);
        
        this.restaurant1.setLocation("Left corner");
        this.restaurant2.setLocation("Right corner");
    }

    @BeforeEach
    void setUp() {
        this.entityManager.clear();

        this.entityManager.persist(this.user1);
        this.entityManager.persist(this.user2);
        this.entityManager.persist(this.user3);
        this.entityManager.persist(this.user4);
        this.entityManager.persist(this.restaurant1);
        this.entityManager.persist(this.restaurant2);
        this.entityManager.flush();
    }

    @Test
    void throwExceptionWhenRepeatEmail() {
        var repeatedUser = new DAOUser();
        repeatedUser.setEmail(this.user1.getEmail());
        repeatedUser.setPassword("password");
        repeatedUser.setRole("ADMIN");

        Assertions.assertThatThrownBy(
            () -> this.userRepository.save(
                repeatedUser
            )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void throwExceptionWhenBadRoleInserted() {
        var emptyRole = new DAOUser();
        emptyRole.setEmail("Someone@mail.com");
        emptyRole.setPassword("asasdas");
        emptyRole.setRole("bla bla");

        Assertions.assertThatThrownBy(
            () -> this.userRepository.save(
                emptyRole
            )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findByEmailShouldReturnOnlyOrlandoWhenISearchForHim() {
        Assertions.assertThat(
            this.userRepository.findByEmail(
                this.user1.getEmail()
            )
        ).isEqualTo(
            Optional.of(
                this.user1
            )
        );
    }

    @Test
    void testClientAddsFavoriteRestaurant() {
        this.user2.addFavoriteRestaurant(this.restaurant1);
        this.user2.addFavoriteRestaurant(this.restaurant2);

        this.user2 = this.userRepository.save(this.user2);

        assertEquals(
            2,
            this.user2.getClientFavoriteRestaurants().size()
        );

        this.user2.removeFavoriteRestaurant(this.restaurant1);
        this.user2 = this.userRepository.save(this.user2);

        assertEquals(
            1,
            this.user2.getClientFavoriteRestaurants().size()
        );
    }
}
