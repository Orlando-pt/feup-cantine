package pt.feup.les.feupfood.repository;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Restaurant;

@DataJpaTest
public class MealRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MealRepository mealRepository;

    private DAOUser user;
    private Restaurant restaurant1;
    private Meal meal1;
    private Meal meal2;
    private Meal meal3;

    @BeforeEach
    void setUp() {
        this.entityManager.clear();

        this.generateMealData();

        this.entityManager.persist(this.user);
        this.entityManager.persist(this.restaurant1);
        this.entityManager.persist(this.meal1);
        this.entityManager.persist(this.meal2);
        this.entityManager.persist(this.meal3);

        this.entityManager.flush();
    }
    @Test
    void testFindMealsByMealType() {
        List<Meal> expectedMeals = Arrays.asList(this.meal1, this.meal2);

        var result = this.mealRepository.findMealsByMealType(MealTypeEnum.DESERT);
        
        Assertions.assertThat(
            result
        ).isEqualTo(expectedMeals);
    }

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void apagar() {
        System.out.println(restaurantRepository.findAll());
    }

    private void generateMealData() {
        this.user = new DAOUser();
        this.user.setFullName("Diogo");
        this.user.setPassword("password");
        this.user.setEmail("email21212@mail.com");
        this.user.setRole("ROLE_USER_RESTAURANT");
        
        this.restaurant1 = new Restaurant();
        this.user.setRestaurant(this.restaurant1);
        this.restaurant1.setOwner(this.user);
        this.restaurant1.setLocation("some place");
        
        this.meal1 = new Meal();
        this.meal1.setMealType(MealTypeEnum.DESERT);
        this.meal1.setDescription("Pudim");
        this.meal1.setRestaurant(this.restaurant1);
        this.restaurant1.addMeal(this.meal1);

        this.meal2 = new Meal();
        this.meal2.setMealType(MealTypeEnum.DESERT);
        this.meal2.setDescription("Mousse de gelatina azeda");
        this.meal2.setRestaurant(this.restaurant1);
        this.restaurant1.addMeal(this.meal2);

        this.meal3 = new Meal();
        this.meal3.setMealType(MealTypeEnum.DIET);
        this.meal3.setDescription("Uma noz");
        this.meal3.setRestaurant(this.restaurant1);
        this.restaurant1.addMeal(this.meal3);
    }
}
