package pt.feup.les.feupfood.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisSessionRepositoryTest {
    
    @Autowired
    private RedisSessionRepository redisRepository;

    private final String user = "useroncache@mail.com";

    @BeforeEach
    void setup() {
        this.redisRepository.removeUser(user);
    }

    @Test
    void whenEmailIsStoredOnCacheThenReturnActive() {
        // verify a user that is not signed in
        Assertions.assertThat(
            this.redisRepository.userIsActive(user)
        ).isNull();
        
        // now lets simulate that the user signed in
        this.redisRepository.addUser(user);
        Assertions.assertThat(
            this.redisRepository.userIsActive(user)
        ).isEqualTo("active");

        // now lets simulate the user signed out
        // and tried to call a endpoint right after
        this.redisRepository.removeUser(user);
        Assertions.assertThat(
            this.redisRepository.userIsActive(user)
        ).isNull();
    }
}
