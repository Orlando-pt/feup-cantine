package pt.feup.les.feupfood.repository;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Restaurant;

@DataJpaTest
public class MenuRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MenuRepository menuRepository;

    private DAOUser user1;
    private Restaurant restaurant1;
    private Meal meal1;
    private Meal meal2;
    private Meal meal3;
    private Menu menu1;
    private Menu menu2;

    @BeforeEach
    void setUp() {
        this.entityManager.clear();
        this.generateMenuData();

        this.entityManager.persist(this.user1);
        this.entityManager.persist(this.restaurant1);
        this.entityManager.persist(this.meal1);
        this.entityManager.persist(this.meal2);
        this.entityManager.persist(this.meal3);
        this.entityManager.persist(this.menu1);
        this.entityManager.persist(this.menu2);

        this.entityManager.flush();
    }

    @Test
    void testFindByName() {
        Assertions.assertThat(
            this.menuRepository.findByName(this.menu2.getName())
        ).isEqualTo(
            Arrays.asList(this.menu2)
        );
    }

    private void generateMenuData() {
        this.user1 = new DAOUser();
        this.user1.setFullName("Diogo");
        this.user1.setPassword("password");
        this.user1.setEmail("email21212@mail.com");
        this.user1.setRole("ROLE_USER_RESTAURANT");
        
        this.restaurant1 = new Restaurant();
        this.user1.setRestaurant(this.restaurant1);
        this.restaurant1.setOwner(this.user1);
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

        this.menu1 = new Menu();
        this.menu1.setName("Monday morning");
        this.menu1.setStartPrice(5.11);
        this.menu1.setEndPrice(10.50);
        this.menu1.addMeal(this.meal1);


        this.menu2 = new Menu();
        this.menu2.setName("Monday afternoon");
        this.menu2.setStartPrice(5.11);
        this.menu2.setEndPrice(10.50);
        this.menu2.addMeal(this.meal2);
        this.menu2.addMeal(this.meal3);
    }
}
