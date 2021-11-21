package pt.feup.les.feupfood.repository;

import javax.persistence.PersistenceException;

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
        this.user1.setUsername("Orlando");
        this.user1.setPassword("SecretPassword");
        this.user1.setRole("ADMIN");
        
        this.user2 = new DAOUser();
        this.user2.setUsername("John");
        this.user2.setPassword("AnotherSecretPassword");
        this.user2.setRole("USER_CLIENT");
    }

    @BeforeEach
    void setUp() {
        this.entityManager.clear();

        this.entityManager.persist(this.user1);
        this.entityManager.persist(this.user2);
        this.entityManager.flush();
    }

    @Test
    void throwExceptionWhenRepeatUsername() {
        var repeatedUser = new DAOUser();
        repeatedUser.setUsername(this.user1.getUsername());
        repeatedUser.setPassword("password");
        repeatedUser.setRole("ADMIN");

        Assertions.assertThatThrownBy(
            () -> this.userRepository.save(
                repeatedUser
            )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void throwExceptionWhenEmptyRole() {
        var emptyRole = new DAOUser();
        emptyRole.setUsername("Someone");
        emptyRole.setPassword("asasdas");
        emptyRole.setRole("bla bla");

        Assertions.assertThatThrownBy(
            () -> this.userRepository.save(
                emptyRole
            )
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}
