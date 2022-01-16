package pt.feup.les.feupfood.runner;

import java.sql.Time;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;
import pt.feup.les.feupfood.model.ScheduleEnum;
import pt.feup.les.feupfood.repository.AssignMenuRepository;
import pt.feup.les.feupfood.repository.EatIntentionRepository;
import pt.feup.les.feupfood.repository.MealRepository;
import pt.feup.les.feupfood.repository.MenuRepository;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.ReviewRepository;
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
    private ReviewRepository reviewRepository;

    @Autowired
    private EatIntentionRepository eatIntentionRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String password = "password";

        DAOUser admin = new DAOUser();
        admin.setEmail("chicao@gmail.com");
        admin.setPassword(
            this.bcryptEncoder.encode(
                password
            )
        );
        admin.setFullName("Chico o grande");
        admin.setRole("ROLE_ADMIN");
        admin.setTerms(true);

        this.userRepository.save(admin);

        DAOUser restaurant = new DAOUser();
        restaurant.setEmail("orlando@gmail.com");
        restaurant.setPassword(
            this.bcryptEncoder.encode(
                password
            )
        );
        restaurant.setFullName("Engineering Canteen");
        restaurant.setProfileImageUrl("https://sigarra.up.pt/sasup/en/imagens/SC-alimentacao-cantina-engenharia.jpg");
        restaurant.setRole("ROLE_USER_RESTAURANT");
        restaurant.setTerms(true);
        restaurant = this.userRepository.save(restaurant);

        Restaurant restaurantObject = new Restaurant();
        restaurantObject.setOwner(restaurant);
        restaurantObject.setLocation("On the corner");
        restaurantObject.setCuisines("Portuguesa, Bar, Europeia, Contemporâneo, Pub, Cervejarias");
        restaurantObject.setTypeMeals("Almoço, Jantar, Bebidas");
        restaurantObject.setMorningOpeningSchedule(
            Time.valueOf("11:00:00")
        );
        restaurantObject.setMorningClosingSchedule(
            Time.valueOf("15:00:00")
        );
        restaurantObject.setAfternoonOpeningSchedule(
            Time.valueOf("18:30:00")
        );
        restaurantObject.setAfternoonClosingSchedule(
            Time.valueOf("22:30:00")
        );
        restaurantObject = this.restaurantRepository.save(restaurantObject);
        
        restaurant.setRestaurant(restaurantObject);
        this.userRepository.save(restaurant);

        DAOUser ownerRestaurant2 = new DAOUser();
        ownerRestaurant2.setEmail("adelaide@gmail.com");
        ownerRestaurant2.setPassword(
            this.bcryptEncoder.encode(
                password
            )
        );
        ownerRestaurant2.setFullName("A tasquinha da Adelaide");
        ownerRestaurant2.setProfileImageUrl("https://culinarybackstreets.com/wp-content/uploads/cb_lisbon_tascaalfama_rc_final.jpg");
        ownerRestaurant2.setRole("ROLE_USER_RESTAURANT");
        ownerRestaurant2.setTerms(true);
        ownerRestaurant2 = this.userRepository.save(ownerRestaurant2);

        Restaurant restaurantAdelaide = new Restaurant();
        restaurantAdelaide.setOwner(ownerRestaurant2);
        restaurantAdelaide.setLocation("Porto, Paranhos, Some Place street");
        restaurantAdelaide.setCuisines("Portuguesa, Bar, Pub, Cervejarias");
        restaurantAdelaide.setTypeMeals("Almoço, Jantar, Bebidas, Lanches");
        restaurantAdelaide.setMorningOpeningSchedule(
            Time.valueOf("09:00:00")
        );
        restaurantAdelaide.setAfternoonClosingSchedule(
            Time.valueOf("23:30:00")
        );
        restaurantAdelaide = this.restaurantRepository.save(restaurantAdelaide);
        
        DAOUser client = new DAOUser();
        client.setEmail("francisco@gmail.com");
        client.setPassword(
            this.bcryptEncoder.encode(
                password
            )
        );
        client.setFullName("Francisco Bastos");
        client.setProfileImageUrl("https://media.istockphoto.com/photos/strong-real-person-real-body-senior-man-proudly-flexing-muscles-picture-id638471524?s=612x612");
        client.setRole("ROLE_USER_CLIENT");
        client.setTerms(true);
        this.userRepository.save(client);
        
        // add review
        Review franciscosReview = new Review();
        franciscosReview.setClassificationGrade(5);
        franciscosReview.setClient(client);
        franciscosReview.setComment("The meat is simple delicious!");
        franciscosReview.setRestaurant(restaurantAdelaide);
        this.reviewRepository.save(franciscosReview);

        Review feupCantineReview = new Review();
        feupCantineReview.setClassificationGrade(2);
        feupCantineReview.setClient(client);
        feupCantineReview.setComment("Who does not like a meal of rice with potato sauce");
        feupCantineReview.setRestaurant(restaurantObject);
        this.reviewRepository.save(feupCantineReview);

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
        fish.setNutritionalInformation("nutritionalInformation1");
        fish.setRestaurant(restaurantObject);
        fish = this.mealRepository.save(fish);

        Meal diet = new Meal();
        diet.setDescription("A carrot");
        diet.setMealType(MealTypeEnum.DIET);
        diet.setNutritionalInformation("nutritionalInformation2");
        diet.setRestaurant(restaurantObject);
        diet = this.mealRepository.save(diet);

        Meal vegetarian = new Meal();
        vegetarian.setDescription("Rice with cogumelos");
        vegetarian.setMealType(MealTypeEnum.VEGETARIAN);
        vegetarian.setNutritionalInformation("nutritionalInformation3");
        vegetarian.setRestaurant(restaurantObject);
        vegetarian = this.mealRepository.save(vegetarian);

        Meal desert = new Meal();
        desert.setDescription("Chocolat mousse");
        desert.setMealType(MealTypeEnum.DESERT);
        desert.setNutritionalInformation("nutritionalInformation4");
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
        menu.setDiscount(0.15);
        menu.addMeal(meat);
        menu.addMeal(fish);
        menu.addMeal(diet);
        menu.addMeal(vegetarian);
        menu.addMeal(desert);
        menu.setRestaurant(restaurantObject);
        menu = this.menuRepository.save(menu);

        Menu menu2 = new Menu();
        menu2.setName("Menu 2");
        menu2.setAdditionalInformation("We put a small portion of sugar in ou food");
        menu2.setEndPrice(5.4);
        menu2.setStartPrice(1.25);
        menu2.setDiscount(0.35);
        menu2.addMeal(meat);
        menu2.addMeal(fish);
        menu2.addMeal(diet);
        menu2.addMeal(vegetarian);
        menu2.addMeal(desert);
        menu2.setRestaurant(restaurantObject);
        this.menuRepository.save(menu2);
        
        AssignMenu assignment = new AssignMenu();
        // set 3 days from now
        long oneDay = 1000L * 60 * 60 * 24;
        assignment.setDate(
            new Date(System.currentTimeMillis() + (3 * oneDay))
        );
        assignment.setMenu(menu);
        assignment.setRestaurant(restaurantObject);
        assignment.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment2 = new AssignMenu();
        assignment2.setDate(
            new Date(System.currentTimeMillis() + oneDay)
        );
        assignment2.setMenu(menu);
        assignment2.setRestaurant(restaurantObject);
        assignment2.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentForToday = new AssignMenu();
        assignmentForToday.setDate(new Date(System.currentTimeMillis()));
        assignmentForToday.setMenu(menu);
        assignmentForToday.setRestaurant(restaurantObject);
        assignmentForToday.setSchedule(ScheduleEnum.LUNCH);

        this.assignMenuRepository.save(assignment);
        this.assignMenuRepository.save(assignment2);
        this.assignMenuRepository.save(assignmentForToday);

        EatIntention eatIntention = new EatIntention();
        eatIntention.setAssignment(assignment);
        eatIntention.setClient(client);
        eatIntention.setCode("123456789");
        eatIntention.setValidatedCode(false);
        eatIntention.setMeals(Set.of(meat));

        this.eatIntentionRepository.save(eatIntention);
    }
    
}
