package pt.feup.les.feupfood.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.ScheduleEnum;

@DataJpaTest
public class EatIntentionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EatIntentionRepository eatIntentionRepository;

    private DAOUser user1;
    private DAOUser client1;
    private Restaurant restaurant1;
    private Meal meal1;
    private Meal meal2;
    private Menu menu1;
    private AssignMenu assignment1;

    private EatIntention intention;

    @BeforeEach
    void setup() {
        this.entityManager.clear();

        this.generateData();
    }

    @Test
    void testIntentionsQueryAll() {

        Assertions.assertThat(
            this.eatIntentionRepository.findAll()
        ).hasSize(1);

    }
    
    @Test
    void testFindByCode() {
        Assertions.assertThat(
            this.eatIntentionRepository.findByCode("123456789")
        ).contains(this.intention);
    }

    private void generateData() {

        this.user1 = new DAOUser();
        this.user1.setFullName("Diogo");
        this.user1.setPassword("password");
        this.user1.setEmail("email2asdasd1212@mail.com");
        this.user1.setRole("ROLE_USER_RESTAURANT");
        
        this.client1 = new DAOUser();
        this.client1.setFullName("Nuno");
        this.client1.setPassword("password");
        this.client1.setEmail("nunofilipe@mail.com");
        this.client1.setRole("ROLE_USER_CLIENT");

        this.entityManager.persist(this.user1);
        this.entityManager.flush();

        this.client1 = this.entityManager.persistAndFlush(this.client1);
        
        this.restaurant1 = new Restaurant();
        this.user1.setRestaurant(this.restaurant1);
        this.restaurant1.setOwner(this.user1);
        this.restaurant1.setLocation("some place");
        
        this.entityManager.persist(this.restaurant1);
        this.entityManager.persist(this.user1);
        this.entityManager.flush();

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

        this.entityManager.persist(this.meal1);
        this.entityManager.persist(this.meal2);
        this.entityManager.persist(this.restaurant1);
        this.entityManager.flush();

        this.menu1 = new Menu();
        this.menu1.setName("Monday morning");
        this.menu1.setStartPrice(5.11);
        this.menu1.setEndPrice(10.50);
        this.menu1.setDiscount(2.10);
        this.menu1.addMeal(this.meal1);
        this.menu1.setRestaurant(this.restaurant1);
        this.menu1.addMeal(this.meal2);

        this.entityManager.persist(this.menu1);
        this.entityManager.persist(this.meal1);
        this.entityManager.persist(this.meal2);
        this.entityManager.flush();

        this.assignment1 = new AssignMenu();
        var formatDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            this.assignment1.setDate(
                new Date(formatDate.parse("2021-12-07").getTime())
            );
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        this.assignment1.setSchedule(ScheduleEnum.LUNCH);
        this.assignment1.setMenu(this.menu1);
        this.menu1.addAssignment(this.assignment1);
        this.assignment1.setRestaurant(this.restaurant1);
        this.restaurant1.addAssignment(this.assignment1);

        this.entityManager.persist(this.assignment1);
        this.entityManager.persist(this.menu1);
        this.entityManager.persist(this.restaurant1);
        this.entityManager.flush();

        this.intention = new EatIntention();
        this.intention.setClient(this.client1);
        this.intention.setAssignment(this.assignment1);
        this.intention.getMeals().add(
            this.assignment1.getMenu().getMeals().get(0)
        );
        this.intention.getMeals().add(
            this.assignment1.getMenu().getMeals().get(1)
        );

        this.intention.setCode("123456789");
        this.intention.setValidatedCode(false);

        this.entityManager.persistAndFlush(this.intention);

    }
}
