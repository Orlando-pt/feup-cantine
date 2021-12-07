package pt.feup.les.feupfood.repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.ScheduleEnum;

@DataJpaTest
public class AssignMenuRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssignMenuRepository assignMenuRepository;

    private DAOUser user1;
    private DAOUser user2;
    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private Meal meal1;
    private Meal meal2;
    private Meal meal3;
    private Menu menu1;
    private Menu menu2;
    private AssignMenu assignment1;
    private AssignMenu assignment2;

    @BeforeEach
    void setup() {
        this.entityManager.clear();

        this.generateAssignMenuData();
    }
    
    @Test
    void testFindByRestaurant() {
        Assertions.assertThat(
            this.assignMenuRepository.findByRestaurant(this.restaurant1)
        ).hasSize(1).isEqualTo(
            Arrays.asList(this.assignment1)
        );
    }

    private void generateAssignMenuData() {
        this.user1 = new DAOUser();
        this.user1.setFullName("Diogo");
        this.user1.setPassword("password");
        this.user1.setEmail("email21212@mail.com");
        this.user1.setRole("ROLE_USER_RESTAURANT");
        
        this.user2 = new DAOUser();
        this.user2.setFullName("André");
        this.user2.setPassword("password");
        this.user2.setEmail("email2121asdasd2@mail.com");
        this.user2.setRole("ROLE_USER_RESTAURANT");

        this.entityManager.persist(this.user1);
        this.entityManager.persist(this.user2);
        this.entityManager.flush();

        this.restaurant1 = new Restaurant();
        this.user1.setRestaurant(this.restaurant1);
        this.restaurant1.setOwner(this.user1);
        this.restaurant1.setLocation("some place");
        
        this.restaurant2 = new Restaurant();
        this.user2.setRestaurant(this.restaurant2);
        this.restaurant2.setOwner(this.user2);
        this.restaurant2.setLocation("some place different from the first");

        this.entityManager.persist(this.restaurant1);
        this.entityManager.persist(this.restaurant2);
        this.entityManager.persist(this.user1);
        this.entityManager.persist(this.user2);
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

        this.meal3 = new Meal();
        this.meal3.setMealType(MealTypeEnum.DIET);
        this.meal3.setDescription("Uma noz");
        this.meal3.setRestaurant(this.restaurant2);
        this.restaurant2.addMeal(this.meal3);

        this.entityManager.persist(this.meal1);
        this.entityManager.persist(this.meal2);
        this.entityManager.persist(this.meal3);
        this.entityManager.persist(this.restaurant1);
        this.entityManager.persist(this.restaurant2);
        this.entityManager.flush();

        this.menu1 = new Menu();
        this.menu1.setName("Monday morning");
        this.menu1.setStartPrice(5.11);
        this.menu1.setEndPrice(10.50);
        this.menu1.addMeal(this.meal1);
        this.menu1.addMeal(this.meal2);


        this.menu2 = new Menu();
        this.menu2.setName("Monday afternoon");
        this.menu2.setStartPrice(5.11);
        this.menu2.setEndPrice(10.50);
        this.menu2.addMeal(this.meal3);

        this.entityManager.persist(this.menu1);
        this.entityManager.persist(this.menu2);
        this.entityManager.persist(this.meal1);
        this.entityManager.persist(this.meal2);
        this.entityManager.persist(this.meal3);
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

        this.assignment2 = new AssignMenu();
        this.assignment2.setDate(this.assignment1.getDate());
        this.assignment2.setSchedule(ScheduleEnum.LUNCH);
        this.assignment2.setMenu(this.menu2);
        this.menu2.addAssignment(this.assignment2);
        this.assignment2.setRestaurant(this.restaurant2);
        this.restaurant2.addAssignment(this.assignment2);

        this.entityManager.persist(this.assignment1);
        this.entityManager.persist(this.assignment2);
        this.entityManager.persist(this.menu1);
        this.entityManager.persist(this.menu2);
        this.entityManager.persist(this.restaurant1);
        this.entityManager.persist(this.restaurant2);
        this.entityManager.flush();
    }
}
