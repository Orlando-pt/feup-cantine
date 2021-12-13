package pt.feup.les.feupfood.runner;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.ScheduleEnum;
import pt.feup.les.feupfood.repository.AssignMenuRepository;
import pt.feup.les.feupfood.repository.MealRepository;
import pt.feup.les.feupfood.repository.MenuRepository;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.UserRepository;

@Component
public class DatabaseRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private AssignMenuRepository assignMenuRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        DAOUser admin = new DAOUser();
        admin.setEmail("chicao@gmail.com");
        admin.setPassword(
            this.bcryptEncoder.encode(
                "password"
            )
        );
        admin.setFullName("Chico o grande");
        admin.setRole("ROLE_ADMIN");
        admin.setTerms(true);

        admin = this.userRepository.save(admin);

        DAOUser restaurant = new DAOUser();
        restaurant.setEmail("orlando@gmail.com");
        restaurant.setPassword(
            this.bcryptEncoder.encode(
                "password"
            )
        );
        restaurant.setFullName("Orlando's Restaurant");
        restaurant.setRole("ROLE_USER_RESTAURANT");
        restaurant.setTerms(true);
        restaurant = this.userRepository.save(restaurant);

        Restaurant restaurantObject = new Restaurant();
        restaurantObject.setOwner(restaurant);
        restaurantObject.setLocation("On the corner");
        restaurantObject = this.restaurantRepository.save(restaurantObject);
        
        restaurant.setRestaurant(restaurantObject);
        restaurant = this.userRepository.save(restaurant);

        
        DAOUser client = new DAOUser();
        client.setEmail("francisco@gmail.com");
        client.setPassword(
            this.bcryptEncoder.encode(
                "password"
            )
        );
        client.setFullName("Francisco Bastos");
        client.setRole("ROLE_USER_CLIENT");
        client.setTerms(true);
        client = this.userRepository.save(client);

        // add meals
        Meal meat = new Meal();
        meat.setDescription("Rice with turkey");
        meat.setMealType(MealTypeEnum.MEAT);
        meat.setNutritionalInformation("very good meat");
        meat.setRestaurant(restaurantObject);
        meat = this.mealRepository.save(meat);
        
        Meal fish = new Meal();
        fish.setDescription("Potatoes with sardines");
        fish.setMealType(MealTypeEnum.FISH);
        fish.setNutritionalInformation("nutritionalInformation");
        fish.setRestaurant(restaurantObject);
        fish = this.mealRepository.save(fish);

        Meal diet = new Meal();
        diet.setDescription("A carrot");
        diet.setMealType(MealTypeEnum.DIET);
        diet.setNutritionalInformation("nutritionalInformation");
        diet.setRestaurant(restaurantObject);
        diet = this.mealRepository.save(diet);

        Meal vegetarian = new Meal();
        vegetarian.setDescription("Rice with cogumelos");
        vegetarian.setMealType(MealTypeEnum.VEGETARIAN);
        vegetarian.setNutritionalInformation("nutritionalInformation");
        vegetarian.setRestaurant(restaurantObject);
        vegetarian = this.mealRepository.save(vegetarian);

        Meal desert = new Meal();
        desert.setDescription("Chocolat mousse");
        desert.setMealType(MealTypeEnum.DESERT);
        desert.setNutritionalInformation("nutritionalInformation");
        desert.setRestaurant(restaurantObject);
        desert = this.mealRepository.save(desert);

        restaurantObject.addMeal(meat);
        restaurantObject.addMeal(fish);
        restaurantObject.addMeal(diet);
        restaurantObject.addMeal(vegetarian);
        restaurantObject.addMeal(desert);
        restaurantObject = this.restaurantRepository.save(restaurantObject);

        Menu menu = new Menu();
        menu.setName("Menu 1");
        menu.setAdditionalInformation("additionalInformation");
        menu.setEndPrice(3.0);
        menu.setStartPrice(2.5);
        menu.addMeal(meat);
        menu.addMeal(fish);
        menu.addMeal(diet);
        menu.addMeal(vegetarian);
        menu.addMeal(desert);
        menu.setRestaurant(restaurantObject);
        menu = this.menuRepository.save(menu);

        meat.addMenu(menu);
        meat = this.mealRepository.save(meat);
        fish.addMenu(menu);
        fish = this.mealRepository.save(fish);
        diet.addMenu(menu);
        diet = this.mealRepository.save(diet);
        vegetarian.addMenu(menu);
        vegetarian = this.mealRepository.save(vegetarian);
        desert.addMenu(menu);
        desert = this.mealRepository.save(desert);

        AssignMenu assignment = new AssignMenu();
        assignment.setDate(
            new Date(1639398815581L)
        );
        assignment.setMenu(menu);
        assignment.setRestaurant(restaurantObject);
        assignment.setSchedule(ScheduleEnum.LUNCH);

        this.assignMenuRepository.save(assignment);
    }
    
}
