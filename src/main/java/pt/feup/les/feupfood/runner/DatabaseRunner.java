package pt.feup.les.feupfood.runner;

import java.sql.Time;
import java.sql.Timestamp;
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
        
        DAOUser client2 = new DAOUser();
        client2.setEmail("mariafernades@gmail.com");
        client2.setPassword(
            this.bcryptEncoder.encode(
                password
            )
        );
        client2.setFullName("Maria Fernandes");
        client2.setProfileImageUrl("https://media.istockphoto.com/photos/strong-real-person-real-body-senior-man-proudly-flexing-muscles-picture-id638471524?s=612x612");
        client2.setRole("ROLE_USER_CLIENT");
        client2.setTerms(true);

        this.userRepository.save(client);
        this.userRepository.save(client2);
        
        // add review
        Review franciscosReview = new Review();
        franciscosReview.setClassificationGrade(5);
        franciscosReview.setClient(client);
        franciscosReview.setComment("The meat is simply delicious!");
        franciscosReview.setRestaurant(restaurantAdelaide);
        franciscosReview.setTimestamp(new Timestamp(System.currentTimeMillis()));
        this.reviewRepository.save(franciscosReview);

        Review feupCantineReview = new Review();
        feupCantineReview.setClassificationGrade(2);
        feupCantineReview.setClient(client);
        feupCantineReview.setComment("Who does not like a meal of rice with potato sauce");
        feupCantineReview.setAnswer("We are very sorry. We will try to do better next time.");
        feupCantineReview.setRestaurant(restaurantObject);
        feupCantineReview.setTimestamp(new Timestamp(System.currentTimeMillis()));
        this.reviewRepository.save(feupCantineReview);

        Review feupCantineReview2 = new Review();
        feupCantineReview2.setClassificationGrade(3);
        feupCantineReview2.setClient(client);
        feupCantineReview2.setComment("Today was a bit better.");
        feupCantineReview2.setRestaurant(restaurantObject);
        feupCantineReview2.setTimestamp(new Timestamp(System.currentTimeMillis()));
        this.reviewRepository.save(feupCantineReview2);

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
        assignment.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignment2RealData = new AssignMenu();
        assignment2RealData.setDate(
            new Date(System.currentTimeMillis() + oneDay)
        );
        assignment2RealData.setMenu(menu);
        assignment2RealData.setRestaurant(restaurantObject);
        assignment2RealData.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentForToday = new AssignMenu();
        assignmentForToday.setDate(new Date(System.currentTimeMillis()));
        assignmentForToday.setMenu(menu);
        assignmentForToday.setRestaurant(restaurantObject);
        assignmentForToday.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu previousAssignment = new AssignMenu();
        previousAssignment.setDate(new Date(System.currentTimeMillis() - (2 * oneDay)));
        previousAssignment.setMenu(menu);
        previousAssignment.setRestaurant(restaurantObject);
        previousAssignment.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu previousAssignment2 = new AssignMenu();
        previousAssignment2.setDate(new Date(System.currentTimeMillis() - (2 * oneDay)));
        previousAssignment2.setMenu(menu);
        previousAssignment2.setRestaurant(restaurantObject);
        previousAssignment2.setSchedule(ScheduleEnum.DINNER);
        AssignMenu assignmentForTodayDinner = new AssignMenu();
        assignmentForTodayDinner.setDate(new Date(System.currentTimeMillis()));
        assignmentForTodayDinner.setMenu(menu);
        assignmentForTodayDinner.setRestaurant(restaurantObject);
        assignmentForTodayDinner.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentForTomorrow = new AssignMenu();
        assignmentForTomorrow.setDate(
            new Date(System.currentTimeMillis() + oneDay)
        );
        assignmentForTomorrow.setMenu(menu);
        assignmentForTomorrow.setRestaurant(restaurantObject);
        assignmentForTomorrow.setSchedule(ScheduleEnum.LUNCH);

        // assignments for 2 days come
        AssignMenu assignment2Days = new AssignMenu();
        assignment2Days.setDate(
            new Date(System.currentTimeMillis() + (2 * oneDay))
        );
        assignment2Days.setMenu(menu);
        assignment2Days.setRestaurant(restaurantObject);
        assignment2Days.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment2DaysDinner = new AssignMenu();
        assignment2DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (2 * oneDay))
        );
        assignment2DaysDinner.setMenu(menu);
        assignment2DaysDinner.setRestaurant(restaurantObject);
        assignment2DaysDinner.setSchedule(ScheduleEnum.DINNER);

        // assignments for 3 days come
        AssignMenu assignment3DaysDinner = new AssignMenu();
        assignment3DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (3 * oneDay))
        );
        assignment3DaysDinner.setMenu(menu);
        assignment3DaysDinner.setRestaurant(restaurantObject);
        assignment3DaysDinner.setSchedule(ScheduleEnum.LUNCH);

        // assignments for 4 days come
        AssignMenu assignment4DaysLunch = new AssignMenu();
        assignment4DaysLunch.setDate(
            new Date(System.currentTimeMillis() + (4 * oneDay))
        );
        assignment4DaysLunch.setMenu(menu);
        assignment4DaysLunch.setRestaurant(restaurantObject);
        assignment4DaysLunch.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment4DaysDinner = new AssignMenu();
        assignment4DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (4 * oneDay))
        );
        assignment4DaysDinner.setMenu(menu);
        assignment4DaysDinner.setRestaurant(restaurantObject);
        assignment4DaysDinner.setSchedule(ScheduleEnum.DINNER);

        // assignments for 5 days come
        AssignMenu assignment5DaysLunch = new AssignMenu();
        assignment5DaysLunch.setDate(
            new Date(System.currentTimeMillis() + (5 * oneDay))
        );
        assignment5DaysLunch.setMenu(menu);
        assignment5DaysLunch.setRestaurant(restaurantObject);
        assignment5DaysLunch.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment5DaysDinner = new AssignMenu();
        assignment5DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (5 * oneDay))
        );
        assignment5DaysDinner.setMenu(menu);
        assignment5DaysDinner.setRestaurant(restaurantObject);
        assignment5DaysDinner.setSchedule(ScheduleEnum.DINNER);

        // assignments for 6 days come
        AssignMenu assignment6DaysLunch = new AssignMenu();
        assignment6DaysLunch.setDate(
            new Date(System.currentTimeMillis() + (6 * oneDay))
        );
        assignment6DaysLunch.setMenu(menu);
        assignment6DaysLunch.setRestaurant(restaurantObject);
        assignment6DaysLunch.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment6DaysDinner = new AssignMenu();
        assignment6DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (6 * oneDay))
        );
        assignment6DaysDinner.setMenu(menu);
        assignment6DaysDinner.setRestaurant(restaurantObject);
        assignment6DaysDinner.setSchedule(ScheduleEnum.DINNER);

        // assignments for 7 days come
        AssignMenu assignment7DaysLunch = new AssignMenu();
        assignment7DaysLunch.setDate(
            new Date(System.currentTimeMillis() + (7 * oneDay))
        );
        assignment7DaysLunch.setMenu(menu);
        assignment7DaysLunch.setRestaurant(restaurantObject);
        assignment7DaysLunch.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment7DaysDinner = new AssignMenu();
        assignment7DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (7 * oneDay))
        );
        assignment7DaysDinner.setMenu(menu);
        assignment7DaysDinner.setRestaurant(restaurantObject);
        assignment7DaysDinner.setSchedule(ScheduleEnum.DINNER);

        // assignments for 8 days come
        AssignMenu assignment8DaysLunch = new AssignMenu();
        assignment8DaysLunch.setDate(
            new Date(System.currentTimeMillis() + (8 * oneDay))
        );
        assignment8DaysLunch.setMenu(menu);
        assignment8DaysLunch.setRestaurant(restaurantObject);
        assignment8DaysLunch.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment8DaysDinner = new AssignMenu();
        assignment8DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (8 * oneDay))
        );
        assignment8DaysDinner.setMenu(menu);
        assignment8DaysDinner.setRestaurant(restaurantObject);
        assignment8DaysDinner.setSchedule(ScheduleEnum.DINNER);

        this.assignMenuRepository.save(assignment);
        this.assignMenuRepository.save(assignment2RealData);
        this.assignMenuRepository.save(assignmentForToday);
        this.assignMenuRepository.save(previousAssignment);
        this.assignMenuRepository.save(previousAssignment2);
        this.assignMenuRepository.save(assignmentForTodayDinner);
        this.assignMenuRepository.save(assignmentForTomorrow);
        this.assignMenuRepository.save(assignment2Days);
        this.assignMenuRepository.save(assignment2DaysDinner);
        this.assignMenuRepository.save(assignment3DaysDinner);
        this.assignMenuRepository.save(assignment4DaysLunch);
        this.assignMenuRepository.save(assignment4DaysDinner);
        this.assignMenuRepository.save(assignment5DaysLunch);
        this.assignMenuRepository.save(assignment5DaysDinner);
        this.assignMenuRepository.save(assignment6DaysLunch);
        this.assignMenuRepository.save(assignment6DaysDinner);
        this.assignMenuRepository.save(assignment7DaysLunch);
        this.assignMenuRepository.save(assignment7DaysDinner);
        this.assignMenuRepository.save(assignment8DaysLunch);
        this.assignMenuRepository.save(assignment8DaysDinner);

        EatIntention eatIntention = new EatIntention();
        eatIntention.setAssignment(assignment);
        eatIntention.setClient(client);
        eatIntention.setCode("123456789");
        eatIntention.setValidatedCode(false);
        eatIntention.setMeals(Set.of(meat));

        EatIntention previousEatIntention = new EatIntention();
        previousEatIntention.setAssignment(previousAssignment);
        previousEatIntention.setClient(client);
        previousEatIntention.setCode("987654321");
        previousEatIntention.setValidatedCode(true);
        previousEatIntention.setMeals(Set.of(meat));

        EatIntention previousEatIntention2 = new EatIntention();
        previousEatIntention2.setAssignment(previousAssignment2);
        previousEatIntention2.setClient(client);
        previousEatIntention2.setCode("987654333");
        previousEatIntention2.setValidatedCode(false);
        previousEatIntention2.setMeals(Set.of(meat));
        
        this.eatIntentionRepository.save(eatIntention);
        this.eatIntentionRepository.save(previousEatIntention);
        this.eatIntentionRepository.save(previousEatIntention2);


        // data to verify stats
        long firstDayOfTheYear = 1640995200000L;
        AssignMenu assignment1 = new AssignMenu();
        assignment1.setDate(new Date(firstDayOfTheYear)); // 2022/01/01
        assignment1.setMenu(menu);
        assignment1.setRestaurant(restaurant.getRestaurant());
        assignment1.setSchedule(ScheduleEnum.LUNCH);
        
        AssignMenu assignment2 = new AssignMenu();
        assignment2.setDate(new Date(firstDayOfTheYear)); // 2022/01/01
        assignment2.setMenu(menu);
        assignment2.setRestaurant(restaurant.getRestaurant());
        assignment2.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignment3 = new AssignMenu();
        assignment3.setDate(new Date(firstDayOfTheYear + oneDay)); // 2022/01/02
        assignment3.setMenu(menu);
        assignment3.setRestaurant(restaurant.getRestaurant());
        assignment3.setSchedule(ScheduleEnum.LUNCH);
        
        AssignMenu assignment4 = new AssignMenu();
        assignment4.setDate(new Date(firstDayOfTheYear + oneDay)); // 2022/01/01
        assignment4.setMenu(menu);
        assignment4.setRestaurant(restaurant.getRestaurant());
        assignment4.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignment5 = new AssignMenu();
        assignment5.setDate(new Date(firstDayOfTheYear + (2 * oneDay))); // 2022/01/03
        assignment5.setMenu(menu);
        assignment5.setRestaurant(restaurant.getRestaurant());
        assignment5.setSchedule(ScheduleEnum.LUNCH);
        
        AssignMenu assignment6 = new AssignMenu();
        assignment6.setDate(new Date(firstDayOfTheYear + (2 * oneDay))); // 2022/01/03
        assignment6.setMenu(menu);
        assignment6.setRestaurant(restaurant.getRestaurant());
        assignment6.setSchedule(ScheduleEnum.DINNER);

        assignment1 = this.assignMenuRepository.save(assignment1);
        assignment2 = this.assignMenuRepository.save(assignment2);
        assignment3 = this.assignMenuRepository.save(assignment3);
        assignment4 = this.assignMenuRepository.save(assignment4);
        assignment5 = this.assignMenuRepository.save(assignment5);
        assignment6 = this.assignMenuRepository.save(assignment6);

        // add intentions
        // in the first day of the year both eaten on dinner and lunch
        EatIntention firstDayIntentionLunch1 = new EatIntention();
        firstDayIntentionLunch1.setAssignment(assignment1);
        firstDayIntentionLunch1.setClient(client);
        firstDayIntentionLunch1.setCode("123456789");
        firstDayIntentionLunch1.setMeals(Set.of(menu.getMeals().get(0)));
        firstDayIntentionLunch1.setValidatedCode(true);

        EatIntention firstDayIntentionLunch2 = new EatIntention();
        firstDayIntentionLunch2.setAssignment(assignment1);
        firstDayIntentionLunch2.setClient(client2);
        firstDayIntentionLunch2.setCode("123456788");
        firstDayIntentionLunch2.setMeals(Set.of(menu.getMeals().get(3)));
        firstDayIntentionLunch2.setValidatedCode(false);

        EatIntention firstDayIntentionDinner1 = new EatIntention();
        firstDayIntentionDinner1.setAssignment(assignment2);
        firstDayIntentionDinner1.setClient(client);
        firstDayIntentionDinner1.setCode("123456787");
        firstDayIntentionDinner1.setMeals(Set.of(menu.getMeals().get(0)));
        firstDayIntentionDinner1.setValidatedCode(true);

        EatIntention firstDayIntentionDinner2 = new EatIntention();
        firstDayIntentionDinner2.setAssignment(assignment2);
        firstDayIntentionDinner2.setClient(client2);
        firstDayIntentionDinner2.setCode("123456786");
        firstDayIntentionDinner2.setMeals(Set.of(menu.getMeals().get(0)));
        firstDayIntentionDinner2.setValidatedCode(true);

        // one the second day only the clint2 went to eat at the cantine for lunch
        EatIntention secondDayIntention = new EatIntention();
        secondDayIntention.setAssignment(assignment3);
        secondDayIntention.setClient(client2);
        secondDayIntention.setCode("123456785");
        secondDayIntention.setMeals(Set.of(menu.getMeals().get(3)));
        secondDayIntention.setValidatedCode(true);
        // one the second day only the clint1 went to eat at the cantine for dinner
        EatIntention secondDayIntentionDinner = new EatIntention();
        secondDayIntentionDinner.setAssignment(assignment4);
        secondDayIntentionDinner.setClient(client);
        secondDayIntentionDinner.setCode("123456784");
        secondDayIntentionDinner.setMeals(Set.of(menu.getMeals().get(0)));
        secondDayIntentionDinner.setValidatedCode(true);

        // on the third day both dinner at lunch
        EatIntention thirdDayLunch1 = new EatIntention();
        thirdDayLunch1.setAssignment(assignment5);
        thirdDayLunch1.setClient(client);
        thirdDayLunch1.setCode("123456783");
        thirdDayLunch1.setMeals(Set.of(menu.getMeals().get(0)));
        thirdDayLunch1.setValidatedCode(true);

        EatIntention thirdDayLunch2 = new EatIntention();
        thirdDayLunch2.setAssignment(assignment5);
        thirdDayLunch2.setClient(client2);
        thirdDayLunch2.setCode("123456782");
        thirdDayLunch2.setMeals(Set.of(menu.getMeals().get(3)));
        thirdDayLunch2.setValidatedCode(true);

        // but only client 1 went for dinner
        EatIntention thirdDayDinner = new EatIntention();
        thirdDayDinner.setAssignment(assignment6);
        thirdDayDinner.setClient(client);
        thirdDayDinner.setCode("123456781");
        thirdDayDinner.setMeals(Set.of(menu.getMeals().get(0)));
        thirdDayDinner.setValidatedCode(true);

        this.eatIntentionRepository.save(firstDayIntentionLunch1);
        this.eatIntentionRepository.save(firstDayIntentionLunch2);
        this.eatIntentionRepository.save(firstDayIntentionDinner1);
        this.eatIntentionRepository.save(firstDayIntentionDinner2);
        this.eatIntentionRepository.save(secondDayIntention);
        this.eatIntentionRepository.save(secondDayIntentionDinner);
        this.eatIntentionRepository.save(thirdDayLunch1);
        this.eatIntentionRepository.save(thirdDayLunch2);
        this.eatIntentionRepository.save(thirdDayDinner);

        // add reviews
        // first day
        Review reviewFirstDay = new Review();
        reviewFirstDay.setClient(client);
        reviewFirstDay.setRestaurant(restaurant.getRestaurant());
        reviewFirstDay.setComment("The food was wonderful. Congratulations on the nicest cuisine in Porto.");
        reviewFirstDay.setTimestamp(new Timestamp(1641049200000L));
        reviewFirstDay.setClassificationGrade(5);

        Review reviewFirstDayClient2 = new Review();
        reviewFirstDayClient2.setClient(client2);
        reviewFirstDayClient2.setRestaurant(restaurant.getRestaurant());
        reviewFirstDayClient2.setComment("Like very much");
        reviewFirstDayClient2.setTimestamp(new Timestamp(1641049200000L));
        reviewFirstDayClient2.setClassificationGrade(5);

        // second day
        Review reviewSecondDay = new Review();
        reviewSecondDay.setClient(client);
        reviewSecondDay.setRestaurant(restaurant.getRestaurant());
        reviewSecondDay.setComment("Still very good, but today the waitress was bit angry with something.");
        reviewSecondDay.setTimestamp(new Timestamp(1641135600000L));
        reviewSecondDay.setClassificationGrade(5);

        Review reviewSecondDayClient2 = new Review();
        reviewSecondDayClient2.setClient(client2);
        reviewSecondDayClient2.setRestaurant(restaurant.getRestaurant());
        reviewSecondDayClient2.setComment("I will lower my classification because of the waitress's posture. She was very rude today.");
        reviewSecondDayClient2.setTimestamp(new Timestamp(1641135600000L));
        reviewSecondDayClient2.setClassificationGrade(4);

        // third day
        Review reviewThirdDay = new Review();
        reviewThirdDay.setClient(client);
        reviewThirdDay.setRestaurant(restaurant.getRestaurant());
        reviewThirdDay.setComment("Today the waitress was just unberable.");
        reviewThirdDay.setTimestamp(new Timestamp(1641222000000L));
        reviewThirdDay.setClassificationGrade(2);

        Review reviewThirdDayClient2 = new Review();
        reviewThirdDayClient2.setClient(client2);
        reviewThirdDayClient2.setRestaurant(restaurant.getRestaurant());
        reviewThirdDayClient2.setComment("The waitress threw me a spoon.");
        reviewThirdDayClient2.setTimestamp(new Timestamp(1641222000000L));
        reviewThirdDayClient2.setClassificationGrade(1);

        this.reviewRepository.save(reviewFirstDay);
        this.reviewRepository.save(reviewFirstDayClient2);
        this.reviewRepository.save(reviewSecondDay);
        this.reviewRepository.save(reviewSecondDayClient2);
        this.reviewRepository.save(reviewThirdDay);
        this.reviewRepository.save(reviewThirdDayClient2);

        client.addFavoriteRestaurant(restaurant.getRestaurant());
        client2.addFavoriteRestaurant(restaurant.getRestaurant());

        this.userRepository.save(client);
        this.userRepository.save(client2);

    }
    
}
