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
        restaurant.setEmail("cantinaDeEngenharia@gmail.com");
        restaurant.setPassword(
            this.bcryptEncoder.encode(
                password
            )
        );
        restaurant.setFullName("Cantina de Engenharia");
        restaurant.setProfileImageUrl("https://sigarra.up.pt/sasup/en/imagens/SC-alimentacao-cantina-engenharia.jpg");
        restaurant.setRole("ROLE_USER_RESTAURANT");
        restaurant.setTerms(true);
        restaurant = this.userRepository.save(restaurant);

        Restaurant restaurantObject = new Restaurant();
        restaurantObject.setOwner(restaurant);
        restaurantObject.setLocation("Rua Dr. Roberto Frias 4200-465 Porto");
        restaurantObject.setCuisines("Portuguesa, Europeia, Contemporâneo");
        restaurantObject.setTypeMeals("Almoço, Jantar, Bebidas");
        restaurantObject.setMorningOpeningSchedule(
            Time.valueOf("11:30:00")
        );
        restaurantObject.setMorningClosingSchedule(
            Time.valueOf("14:00:00")
        );
        restaurantObject.setAfternoonOpeningSchedule(
            Time.valueOf("18:30:00")
        );
        restaurantObject.setAfternoonClosingSchedule(
            Time.valueOf("20:30:00")
        );
        restaurantObject = this.restaurantRepository.save(restaurantObject);
        
        restaurant.setRestaurant(restaurantObject);
        this.userRepository.save(restaurant);

        DAOUser ownerRestaurant2 = new DAOUser();
        ownerRestaurant2.setEmail("grillDeEngenharia@gmail.com");
        ownerRestaurant2.setPassword(
            this.bcryptEncoder.encode(
                password
            )
        );
        ownerRestaurant2.setFullName("Grill de Engenharia");
        ownerRestaurant2.setProfileImageUrl("https://sigarra.up.pt/sasup/pt/imagens/SC-alimentacao-grill-engenharia-renovado.jpg");
        ownerRestaurant2.setRole("ROLE_USER_RESTAURANT");
        ownerRestaurant2.setTerms(true);
        ownerRestaurant2 = this.userRepository.save(ownerRestaurant2);

        Restaurant restaurantAdelaide = new Restaurant();
        restaurantAdelaide.setOwner(ownerRestaurant2);
        restaurantAdelaide.setLocation("Rua Dr. Roberto Frias 4200-465 Porto");
        restaurantAdelaide.setCuisines("Grelhados");
        restaurantAdelaide.setTypeMeals("Almoço, Jantar, Bebidas");
        restaurantAdelaide.setMorningOpeningSchedule(
            Time.valueOf("12:00:00")
        );
        restaurantAdelaide.setMorningClosingSchedule(
            Time.valueOf("14:00:00")
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
        client.setProfileImageUrl("https://gitlab.com/uploads/-/system/user/avatar/10100350/avatar.png");
        client.setRole("ROLE_USER_CLIENT");
        client.setTerms(true);
        
        DAOUser client2 = new DAOUser();
        client2.setEmail("orlando@gmail.com");
        client2.setPassword(
            this.bcryptEncoder.encode(
                password
            )
        );
        client2.setFullName("Orlando Macedo");
        client2.setProfileImageUrl("https://orlandomacedo.info/images/urlando.jpg");
        client2.setRole("ROLE_USER_CLIENT");
        client2.setTerms(true);

        this.userRepository.save(client);
        this.userRepository.save(client2);
        
        // add review
        Review franciscosReview = new Review();
        franciscosReview.setClassificationGrade(5);
        franciscosReview.setClient(client);
        franciscosReview.setComment("A comida é deliciosa!");
        franciscosReview.setRestaurant(restaurantAdelaide);
        franciscosReview.setTimestamp(new Timestamp(System.currentTimeMillis()));
        this.reviewRepository.save(franciscosReview);

        Review feupCantineReview = new Review();
        feupCantineReview.setClassificationGrade(2);
        feupCantineReview.setClient(client);
        feupCantineReview.setComment("Lamentável! Hoje o prato era arroz com molho de tomate.");
        feupCantineReview.setAnswer("Lamentamos imenso. Tentaremos melhorar.");
        feupCantineReview.setRestaurant(restaurantObject);
        feupCantineReview.setTimestamp(new Timestamp(System.currentTimeMillis() - (1000L * 60 * 60 * 24)));
        this.reviewRepository.save(feupCantineReview);

        Review feupCantineReview2 = new Review();
        feupCantineReview2.setClassificationGrade(3);
        feupCantineReview2.setClient(client);
        feupCantineReview2.setComment("Devo admitir que fiquei surpreendido. Hoje a refeição estava minimamente apresentável.");
        feupCantineReview2.setRestaurant(restaurantObject);
        feupCantineReview2.setTimestamp(new Timestamp(System.currentTimeMillis()));
        this.reviewRepository.save(feupCantineReview2);

        // add meals
        Meal meat = new Meal();
        meat.setDescription("Pá de porco estufada com ervilhas e arroz de tomate");
        meat.setMealType(MealTypeEnum.MEAT);
        meat.setNutritionalInformation("Kcal: 217; Lip(g): 9.6; HC(g): 19.0; Açucar(g): 0.3; Prot(g): 13.0; Sal(g): 0.3");
        meat.setRestaurant(restaurantObject);
        meat = this.mealRepository.save(meat);
        
        Meal fish = new Meal();
        fish.setDescription("Lombinhos de escamudo gratinado com puré de batata");
        fish.setMealType(MealTypeEnum.FISH);
        fish.setNutritionalInformation("Kcal: 100; Lip(g): 0.6; HC(g): 9.2; Açucar(g): 0.3; Prot(g): 13.2; Sal(g): 0.7");
        fish.setRestaurant(restaurantObject);
        fish = this.mealRepository.save(fish);

        Meal diet = new Meal();
        diet.setDescription("Cozido simples de frango com batata e couve branca cozida");
        diet.setMealType(MealTypeEnum.DIET);
        diet.setNutritionalInformation("Kcal: 131; Lip(g): 3.2; HC(g): 14.2; Açucar(g): 0.9; Prot(g): 10.2; Sal(g): 0.2");
        diet.setRestaurant(restaurantObject);
        diet = this.mealRepository.save(diet);

        Meal vegetarian = new Meal();
        vegetarian.setDescription("Rancho vegan (grão-de-bico, macarrão, couve branca, cenoura)");
        vegetarian.setMealType(MealTypeEnum.VEGETARIAN);
        vegetarian.setNutritionalInformation("Kcal: 164; Lip(g): 2.2; HC(g): 27.2; Açucar(g): 2.9; Prot(g): 6.2; Sal(g): 0.1");
        vegetarian.setRestaurant(restaurantObject);
        vegetarian = this.mealRepository.save(vegetarian);

        Meal desert = new Meal();
        desert.setDescription("Mousse de chocolate");
        desert.setMealType(MealTypeEnum.DESERT);
        desert.setNutritionalInformation("Kcal: 365; Lip(g): 25.2; HC(g): 27.2; Açucar(g): 25.9; Prot(g): 7.2; Sal(g): 0.1");
        desert.setRestaurant(restaurantObject);
        desert = this.mealRepository.save(desert);

        restaurantObject.addMeal(meat);
        restaurantObject.addMeal(fish);
        restaurantObject.addMeal(diet);
        restaurantObject.addMeal(vegetarian);
        restaurantObject.addMeal(desert);
        restaurantObject = this.restaurantRepository.save(restaurantObject);

        // add more meals to the engineer cantine
        Meal meat2 = new Meal();
        meat2.setDescription("Frango crocante com corn flakes com arroz de ervilhas");
        meat2.setMealType(MealTypeEnum.MEAT);
        meat2.setNutritionalInformation("Kcal: 217; Lip(g): 9.6; HC(g): 19.0; Açucar(g): 0.3; Prot(g): 13.0; Sal(g): 0.3");
        meat2.setRestaurant(restaurantObject);
        meat2 = this.mealRepository.save(meat2);
        
        Meal fish2 = new Meal();
        fish2.setDescription("Cavala assada com cebolada e batata cozida");
        fish2.setMealType(MealTypeEnum.FISH);
        fish2.setNutritionalInformation("Kcal: 100; Lip(g): 0.6; HC(g): 9.2; Açucar(g): 0.3; Prot(g): 13.2; Sal(g): 0.7");
        fish2.setRestaurant(restaurantObject);
        fish2 = this.mealRepository.save(fish2);

        Meal diet2 = new Meal();
        diet2.setDescription("Perna de frango grelhada com massa espiral");
        diet2.setMealType(MealTypeEnum.DIET);
        diet2.setNutritionalInformation("Kcal: 131; Lip(g): 3.2; HC(g): 14.2; Açucar(g): 0.9; Prot(g): 10.2; Sal(g): 0.2");
        diet2.setRestaurant(restaurantObject);
        diet2 = this.mealRepository.save(diet2);

        Meal vegetarian2 = new Meal();
        vegetarian2.setDescription("Hambúrguer de grão com esmagada de batata");
        vegetarian2.setMealType(MealTypeEnum.VEGETARIAN);
        vegetarian2.setNutritionalInformation("Kcal: 164; Lip(g): 2.2; HC(g): 27.2; Açucar(g): 2.9; Prot(g): 6.2; Sal(g): 0.1");
        vegetarian2.setRestaurant(restaurantObject);
        vegetarian2 = this.mealRepository.save(vegetarian2);

        Meal desert2 = new Meal();
        desert2.setDescription("Pêra assada");
        desert2.setMealType(MealTypeEnum.DESERT);
        desert2.setNutritionalInformation("Kcal: 365; Lip(g): 25.2; HC(g): 27.2; Açucar(g): 25.9; Prot(g): 7.2; Sal(g): 0.1");
        desert2.setRestaurant(restaurantObject);
        desert2 = this.mealRepository.save(desert2);

        Meal meat3 = new Meal();
        meat3.setDescription("Canelones de carne de vaca");
        meat3.setMealType(MealTypeEnum.MEAT);
        meat3.setNutritionalInformation("Kcal: 217; Lip(g): 9.6; HC(g): 19.0; Açucar(g): 0.3; Prot(g): 13.0; Sal(g): 0.3");
        meat3.setRestaurant(restaurantObject);
        meat3 = this.mealRepository.save(meat3);
        
        Meal fish3 = new Meal();
        fish3.setDescription("Red fish assado à portuguesa com arroz");
        fish3.setMealType(MealTypeEnum.FISH);
        fish3.setNutritionalInformation("Kcal: 100; Lip(g): 0.6; HC(g): 9.2; Açucar(g): 0.3; Prot(g): 13.2; Sal(g): 0.7");
        fish3.setRestaurant(restaurantObject);
        fish3 = this.mealRepository.save(fish3);

        Meal diet3 = new Meal();
        diet3.setDescription("Carapau grelhado com arroz");
        diet3.setMealType(MealTypeEnum.DIET);
        diet3.setNutritionalInformation("Kcal: 131; Lip(g): 3.2; HC(g): 14.2; Açucar(g): 0.9; Prot(g): 10.2; Sal(g): 0.2");
        diet3.setRestaurant(restaurantObject);
        diet3 = this.mealRepository.save(diet3);

        Meal vegetarian3 = new Meal();
        vegetarian3.setDescription("Favas estufadas com coentros e batata cozida");
        vegetarian3.setMealType(MealTypeEnum.VEGETARIAN);
        vegetarian3.setNutritionalInformation("Kcal: 164; Lip(g): 2.2; HC(g): 27.2; Açucar(g): 2.9; Prot(g): 6.2; Sal(g): 0.1");
        vegetarian3.setRestaurant(restaurantObject);
        vegetarian3 = this.mealRepository.save(vegetarian3);

        Meal desert3 = new Meal();
        desert3.setDescription("Ananás");
        desert3.setMealType(MealTypeEnum.DESERT);
        desert3.setNutritionalInformation("Kcal: 365; Lip(g): 25.2; HC(g): 27.2; Açucar(g): 25.9; Prot(g): 7.2; Sal(g): 0.1");
        desert3.setRestaurant(restaurantObject);
        desert3 = this.mealRepository.save(desert3);

        Menu menu = new Menu();
        menu.setName("Menu 1");
        menu.setAdditionalInformation("Dispõe ainda de sopa de curgete");
        menu.setEndPrice(4.0);
        menu.setStartPrice(2.75);
        menu.setDiscount(0.15);
        menu.addMeal(meat);
        menu.addMeal(fish);
        menu.addMeal(diet);
        menu.addMeal(vegetarian);
        menu.addMeal(desert);
        menu.setRestaurant(restaurantObject);
        menu = this.menuRepository.save(menu);

        Menu menu3 = new Menu();
        menu3.setName("Menu 2");
        menu3.setAdditionalInformation("Dispõe ainda de sopa de cebola com juliana de couve");
        menu3.setEndPrice(4.0);
        menu3.setStartPrice(2.75);
        menu3.setDiscount(0.15);
        menu3.addMeal(meat2);
        menu3.addMeal(fish2);
        menu3.addMeal(diet2);
        menu3.addMeal(vegetarian2);
        menu3.addMeal(desert2);
        menu3.setRestaurant(restaurantObject);
        menu3 = this.menuRepository.save(menu3);

        Menu menu4 = new Menu();
        menu4.setName("Menu 3");
        menu4.setAdditionalInformation("Dispõe ainda de sopa de brócolos");
        menu4.setEndPrice(4.0);
        menu4.setStartPrice(2.75);
        menu4.setDiscount(0.15);
        menu4.addMeal(meat3);
        menu4.addMeal(fish3);
        menu4.addMeal(diet3);
        menu4.addMeal(vegetarian3);
        menu4.addMeal(desert3);
        menu4.setRestaurant(restaurantObject);
        menu4 = this.menuRepository.save(menu4);
        
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
        assignment2RealData.setMenu(menu3);
        assignment2RealData.setRestaurant(restaurantObject);
        assignment2RealData.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentForToday = new AssignMenu();
        assignmentForToday.setDate(new Date(System.currentTimeMillis()));
        assignmentForToday.setMenu(menu4);
        assignmentForToday.setRestaurant(restaurantObject);
        assignmentForToday.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu previousAssignment = new AssignMenu();
        previousAssignment.setDate(new Date(System.currentTimeMillis() - (2 * oneDay)));
        previousAssignment.setMenu(menu);
        previousAssignment.setRestaurant(restaurantObject);
        previousAssignment.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu previousAssignment2 = new AssignMenu();
        previousAssignment2.setDate(new Date(System.currentTimeMillis() - (2 * oneDay)));
        previousAssignment2.setMenu(menu3);
        previousAssignment2.setRestaurant(restaurantObject);
        previousAssignment2.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentForTodayDinner = new AssignMenu();
        assignmentForTodayDinner.setDate(new Date(System.currentTimeMillis()));
        assignmentForTodayDinner.setMenu(menu4);
        assignmentForTodayDinner.setRestaurant(restaurantObject);
        assignmentForTodayDinner.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentForTomorrow = new AssignMenu();
        assignmentForTomorrow.setDate(
            new Date(System.currentTimeMillis() + oneDay)
        );
        assignmentForTomorrow.setMenu(menu3);
        assignmentForTomorrow.setRestaurant(restaurantObject);
        assignmentForTomorrow.setSchedule(ScheduleEnum.LUNCH);

        // assignments for 2 days come
        AssignMenu assignment2Days = new AssignMenu();
        assignment2Days.setDate(
            new Date(System.currentTimeMillis() + (2 * oneDay))
        );
        assignment2Days.setMenu(menu4);
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
        assignment3DaysDinner.setMenu(menu3);
        assignment3DaysDinner.setRestaurant(restaurantObject);
        assignment3DaysDinner.setSchedule(ScheduleEnum.LUNCH);

        // assignments for 4 days come
        AssignMenu assignment4DaysLunch = new AssignMenu();
        assignment4DaysLunch.setDate(
            new Date(System.currentTimeMillis() + (4 * oneDay))
        );
        assignment4DaysLunch.setMenu(menu4);
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
        assignment5DaysLunch.setMenu(menu3);
        assignment5DaysLunch.setRestaurant(restaurantObject);
        assignment5DaysLunch.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment5DaysDinner = new AssignMenu();
        assignment5DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (5 * oneDay))
        );
        assignment5DaysDinner.setMenu(menu4);
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
        assignment6DaysDinner.setMenu(menu3);
        assignment6DaysDinner.setRestaurant(restaurantObject);
        assignment6DaysDinner.setSchedule(ScheduleEnum.DINNER);

        // assignments for 7 days come
        AssignMenu assignment7DaysLunch = new AssignMenu();
        assignment7DaysLunch.setDate(
            new Date(System.currentTimeMillis() + (7 * oneDay))
        );
        assignment7DaysLunch.setMenu(menu4);
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
        assignment8DaysLunch.setMenu(menu3);
        assignment8DaysLunch.setRestaurant(restaurantObject);
        assignment8DaysLunch.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignment8DaysDinner = new AssignMenu();
        assignment8DaysDinner.setDate(
            new Date(System.currentTimeMillis() + (8 * oneDay))
        );
        assignment8DaysDinner.setMenu(menu4);
        assignment8DaysDinner.setRestaurant(restaurantObject);
        assignment8DaysDinner.setSchedule(ScheduleEnum.DINNER);

        this.assignMenuRepository.save(assignment);
        this.assignMenuRepository.save(assignment2RealData);
        assignmentForToday = this.assignMenuRepository.save(assignmentForToday);
        this.assignMenuRepository.save(previousAssignment);
        this.assignMenuRepository.save(previousAssignment2);
        assignmentForTodayDinner = this.assignMenuRepository.save(assignmentForTodayDinner);
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

        // add intention for today
        EatIntention eatIntentionForToday = new EatIntention();
        eatIntentionForToday.setAssignment(assignmentForToday);
        eatIntentionForToday.setClient(client);
        eatIntentionForToday.setCode("231222432");
        eatIntentionForToday.setMeals(Set.of(
            assignmentForToday.getMenu().getMeals().get(0)
        ));
        eatIntentionForToday.setValidatedCode(false);

        EatIntention eatIntentionForTodayDinner = new EatIntention();
        eatIntentionForTodayDinner.setAssignment(assignmentForTodayDinner);
        eatIntentionForTodayDinner.setClient(client);
        eatIntentionForTodayDinner.setCode("231333432");
        eatIntentionForTodayDinner.setMeals(Set.of(
            assignmentForTodayDinner.getMenu().getMeals().get(0)
        ));
        eatIntentionForTodayDinner.setValidatedCode(false);

        this.eatIntentionRepository.save(eatIntentionForToday);
        this.eatIntentionRepository.save(eatIntentionForTodayDinner);

        // add meals for the grill cantine

        // add meals
        Meal meatGrill = new Meal();
        meatGrill.setDescription("Novilho estufado com cenoura, ervilhas e esparguete salteado");
        meatGrill.setMealType(MealTypeEnum.MEAT);
        meatGrill.setNutritionalInformation("Kcal: 217; Lip(g): 9.6; HC(g): 19.0; Açucar(g): 0.3; Prot(g): 13.0; Sal(g): 0.3");
        meatGrill.setRestaurant(restaurantAdelaide);
        meatGrill = this.mealRepository.save(meatGrill);
        
        Meal fishGrill = new Meal();
        fishGrill.setDescription("Raia grelhada com molho verde e batata cozida");
        fishGrill.setMealType(MealTypeEnum.FISH);
        fishGrill.setNutritionalInformation("Kcal: 100; Lip(g): 0.6; HC(g): 9.2; Açucar(g): 0.3; Prot(g): 13.2; Sal(g): 0.7");
        fishGrill.setRestaurant(restaurantAdelaide);
        fishGrill = this.mealRepository.save(fishGrill);

        Meal vegetarianGrill = new Meal();
        vegetarianGrill.setDescription("Gratinado de couve em camadas com soja e arroz");
        vegetarianGrill.setMealType(MealTypeEnum.VEGETARIAN);
        vegetarianGrill.setNutritionalInformation("Kcal: 164; Lip(g): 2.2; HC(g): 27.2; Açucar(g): 2.9; Prot(g): 6.2; Sal(g): 0.1");
        vegetarianGrill.setRestaurant(restaurantAdelaide);
        vegetarianGrill = this.mealRepository.save(vegetarianGrill);

        Meal desertGrill = new Meal();
        desertGrill.setDescription("Mousse de chocolate");
        desertGrill.setMealType(MealTypeEnum.DESERT);
        desertGrill.setNutritionalInformation("Kcal: 365; Lip(g): 25.2; HC(g): 27.2; Açucar(g): 25.9; Prot(g): 7.2; Sal(g): 0.1");
        desertGrill.setRestaurant(restaurantAdelaide);
        desertGrill = this.mealRepository.save(desertGrill);

        Menu menuGrill = new Menu();
        menuGrill.setName("Menu 1");
        menuGrill.setAdditionalInformation("Dispõe ainda de sopa de couve lombarda e feijão vermelho");
        menuGrill.setEndPrice(5.0);
        menuGrill.setStartPrice(3.75);
        menuGrill.setDiscount(0.20);
        menuGrill.addMeal(meatGrill);
        menuGrill.addMeal(fishGrill);
        menuGrill.addMeal(vegetarianGrill);
        menuGrill.addMeal(desertGrill);
        menuGrill.setRestaurant(restaurantAdelaide);
        menuGrill = this.menuRepository.save(menuGrill);

        AssignMenu assignmentGrill = new AssignMenu();
        // set 3 days from now
        assignmentGrill.setDate(
            new Date(System.currentTimeMillis() + (1 * oneDay))
        );
        assignmentGrill.setMenu(menuGrill);
        assignmentGrill.setRestaurant(restaurantAdelaide);
        assignmentGrill.setSchedule(ScheduleEnum.DINNER);
        this.assignMenuRepository.save(assignmentGrill);

        AssignMenu assignmentGrillDinner = new AssignMenu();
        // set 3 days from now
        assignmentGrillDinner.setDate(
            new Date(System.currentTimeMillis() + (1 * oneDay))
        );
        assignmentGrillDinner.setMenu(menuGrill);
        assignmentGrillDinner.setRestaurant(restaurantAdelaide);
        assignmentGrillDinner.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignmentGrill2Lunch = new AssignMenu();
        // set 3 days from now
        assignmentGrill2Lunch.setDate(
            new Date(System.currentTimeMillis() + (2 * oneDay))
        );
        assignmentGrill2Lunch.setMenu(menuGrill);
        assignmentGrill2Lunch.setRestaurant(restaurantAdelaide);
        assignmentGrill2Lunch.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentGrill2Dinner = new AssignMenu();
        // set 3 days from now
        assignmentGrill2Dinner.setDate(
            new Date(System.currentTimeMillis() + (2 * oneDay))
        );
        assignmentGrill2Dinner.setMenu(menuGrill);
        assignmentGrill2Dinner.setRestaurant(restaurantAdelaide);
        assignmentGrill2Dinner.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignmentGrill3Lunch = new AssignMenu();
        // set 3 days from now
        assignmentGrill3Lunch.setDate(
            new Date(System.currentTimeMillis() + (3 * oneDay))
        );
        assignmentGrill3Lunch.setMenu(menuGrill);
        assignmentGrill3Lunch.setRestaurant(restaurantAdelaide);
        assignmentGrill3Lunch.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentGrill3Dinner = new AssignMenu();
        // set 3 days from now
        assignmentGrill3Dinner.setDate(
            new Date(System.currentTimeMillis() + (3 * oneDay))
        );
        assignmentGrill3Dinner.setMenu(menuGrill);
        assignmentGrill3Dinner.setRestaurant(restaurantAdelaide);
        assignmentGrill3Dinner.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignmentGrill4Lunch = new AssignMenu();
        // set 3 days from now
        assignmentGrill4Lunch.setDate(
            new Date(System.currentTimeMillis() + (4 * oneDay))
        );
        assignmentGrill4Lunch.setMenu(menuGrill);
        assignmentGrill4Lunch.setRestaurant(restaurantAdelaide);
        assignmentGrill4Lunch.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentGrill4Dinner = new AssignMenu();
        // set 3 days from now
        assignmentGrill4Dinner.setDate(
            new Date(System.currentTimeMillis() + (4 * oneDay))
        );
        assignmentGrill4Dinner.setMenu(menuGrill);
        assignmentGrill4Dinner.setRestaurant(restaurantAdelaide);
        assignmentGrill4Dinner.setSchedule(ScheduleEnum.LUNCH);

        AssignMenu assignmentGrill5Lunch = new AssignMenu();
        // set 3 days from now
        assignmentGrill5Lunch.setDate(
            new Date(System.currentTimeMillis() + (5 * oneDay))
        );
        assignmentGrill5Lunch.setMenu(menuGrill);
        assignmentGrill5Lunch.setRestaurant(restaurantAdelaide);
        assignmentGrill5Lunch.setSchedule(ScheduleEnum.DINNER);

        AssignMenu assignmentGrill5Dinner = new AssignMenu();
        // set 3 days from now
        assignmentGrill5Dinner.setDate(
            new Date(System.currentTimeMillis() + (5 * oneDay))
        );
        assignmentGrill5Dinner.setMenu(menuGrill);
        assignmentGrill5Dinner.setRestaurant(restaurantAdelaide);
        assignmentGrill5Dinner.setSchedule(ScheduleEnum.LUNCH);

        this.assignMenuRepository.save(assignmentGrill);
        this.assignMenuRepository.save(assignmentGrillDinner);
        this.assignMenuRepository.save(assignmentGrill2Dinner);
        this.assignMenuRepository.save(assignmentGrill2Lunch);
        this.assignMenuRepository.save(assignmentGrill3Dinner);
        this.assignMenuRepository.save(assignmentGrill3Lunch);
        this.assignMenuRepository.save(assignmentGrill4Dinner);
        this.assignMenuRepository.save(assignmentGrill4Lunch);
        this.assignMenuRepository.save(assignmentGrill5Dinner);
        this.assignMenuRepository.save(assignmentGrill5Lunch);

    }
    
}
