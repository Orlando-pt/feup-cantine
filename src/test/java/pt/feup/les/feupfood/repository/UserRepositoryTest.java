package pt.feup.les.feupfood.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import pt.feup.les.feupfood.model.DAOUser;

@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private DAOUser user1;
    private DAOUser user2;

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
    }

    @BeforeEach
    void setUp() {
        this.entityManager.clear();

        this.entityManager.persist(this.user1);
        this.entityManager.persist(this.user2);
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
}
