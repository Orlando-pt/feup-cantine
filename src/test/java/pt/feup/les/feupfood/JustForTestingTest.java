package pt.feup.les.feupfood;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JustForTestingTest {
    
    private JustForTesting test;

    @BeforeEach
    void setUp() {
        this.test = new JustForTesting();
    }

    @Test
    void testPositive() {
        assertTrue(
            this.test.isPositive(1)
        );
    }
}
