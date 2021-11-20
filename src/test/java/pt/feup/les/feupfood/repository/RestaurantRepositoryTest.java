package pt.feup.les.feupfood.repository;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pt.feup.les.feupfood.model.Restaurant;

@DataJpaTest
public class RestaurantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurantAlzira;
    private Restaurant restaurantDeolinda;

    public RestaurantRepositoryTest() {
        this.restaurantAlzira = new Restaurant();
        this.restaurantDeolinda = new Restaurant();

        this.restaurantAlzira.setName("Restaurant Dona Alzira");
        this.restaurantAlzira.setLocation("In the corner");

        this.restaurantDeolinda.setName("Restaurant Dona Deolinda");
        this.restaurantDeolinda.setLocation("In the other corner");
    }

    @BeforeEach
    void setUp() {
        this.entityManager.clear();
    }

    @Test
    void whenFindDonaAlzira_shouldOnlyReturnDonaAlzira() {
        this.entityManager.persist(restaurantAlzira);
        this.entityManager.persist(restaurantDeolinda);
        this.entityManager.flush();

        var expectedResult = new ArrayList<Restaurant>();
        expectedResult.add(this.restaurantAlzira);

        Assertions.assertThat(
            this.restaurantRepository.findByName(
                this.restaurantAlzira.getName()
            )
        ).isEqualTo(expectedResult);
    }
    

}
