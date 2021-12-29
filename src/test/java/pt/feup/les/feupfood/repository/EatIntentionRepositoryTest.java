package pt.feup.les.feupfood.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class EatIntentionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EatIntentionRepository eatIntentionRepository;

    @BeforeEach
    void setup() {
        this.entityManager.clear();

        this.generateData();
    }

    @Test
    void apagar() {

        Assertions.assertThat(
            true
        ).isTrue();
    }

    private void generateData() {

    }
}
