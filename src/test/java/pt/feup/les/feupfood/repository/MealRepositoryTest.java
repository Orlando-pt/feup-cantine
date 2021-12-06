package pt.feup.les.feupfood.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Restaurant;

@DataJpaTest
public class MealRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MealRepository mealRepository;

    private Restaurant restaurant1;
    private Meal meal1;
    private Meal meal2;
    private Meal meal3;

    @BeforeEach
    void setUp() {
        this.entityManager.clear();


    }
    @Test
    void testFindMealsByMealType() {

    }

    private void generateMealData() {

    }
}
