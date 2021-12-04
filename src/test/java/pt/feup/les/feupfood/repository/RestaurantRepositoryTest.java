package pt.feup.les.feupfood.repository;

import java.util.ArrayList;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;

@DataJpaTest
public class RestaurantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant restaurantAlzira;
    private Restaurant restaurantDeolinda;
    
    private DAOUser ownerAlzira;
    private DAOUser ownerDeolinda;

    public RestaurantRepositoryTest() {
        this.restaurantAlzira = new Restaurant();
        this.restaurantDeolinda = new Restaurant();

        this.ownerAlzira = new DAOUser();
        this.ownerDeolinda = new DAOUser();

        this.ownerAlzira.setFullName("Alzira Esteves");
        this.ownerAlzira.setPassword("password");
        this.ownerAlzira.setEmail("email");
        this.ownerAlzira.setRole("ROLE_USER_RESTAURANT");
        this.ownerAlzira.setTerms(true);


        this.ownerDeolinda.setFullName("Deolinda Macarrão");
        this.ownerDeolinda.setPassword("password");
        this.ownerDeolinda.setEmail("emaildadeolinda");
        this.ownerDeolinda.setRole("ROLE_USER_RESTAURANT");
        this.ownerDeolinda.setTerms(true);

        this.restaurantAlzira.setName("Restaurant Dona Alzira");
        this.restaurantAlzira.setOwner(this.ownerAlzira);
        this.restaurantAlzira.setLocation("In the corner");

        this.restaurantDeolinda.setName("Restaurant Dona Deolinda");
        this.restaurantDeolinda.setOwner(this.ownerDeolinda);
        this.restaurantDeolinda.setLocation("In the other corner");

        this.ownerAlzira.setRestaurant(restaurantAlzira);
        this.ownerDeolinda.setRestaurant(restaurantDeolinda);
    }

    @BeforeEach
    void setUp() {
        this.entityManager.clear();

        this.entityManager.persist(this.ownerAlzira);
        this.entityManager.persist(this.ownerDeolinda);

        this.entityManager.persist(this.restaurantAlzira);
        this.entityManager.persist(this.restaurantDeolinda);
        this.entityManager.flush();
    }

    @Test
    void whenFindDonaAlzira_shouldOnlyReturnDonaAlzira() {
        var expectedResult = new ArrayList<Restaurant>();
        expectedResult.add(this.restaurantAlzira);

        Assertions.assertThat(
            this.restaurantRepository.findByName(
                this.restaurantAlzira.getName()
            )
        ).isEqualTo(expectedResult);
    }

    @Test
    void findByOwnerTest() {
        Assertions.assertThat(
            this.restaurantRepository.findByOwner(
                this.ownerAlzira
            )
        ).isEqualTo(
            Optional.of(this.restaurantAlzira)
        );
    }

    @Test
    void fromUserGetRestaurant() {
        Assertions.assertThat(
            this.ownerDeolinda.getRestaurant()
        ).isEqualTo(
            this.restaurantDeolinda
        );
    }
    

}
