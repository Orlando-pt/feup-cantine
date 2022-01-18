package pt.feup.les.feupfood.controller;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pt.feup.les.feupfood.dto.AddAssignmentDto;
import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.AddMenuDto;
import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.GetClientReviewDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
import pt.feup.les.feupfood.dto.JwtRequest;
import pt.feup.les.feupfood.dto.JwtResponse;
import pt.feup.les.feupfood.dto.RegisterUserDto;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;
import pt.feup.les.feupfood.dto.RestaurantAnswerReviewDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.dto.StatsIntentionDto;
import pt.feup.les.feupfood.dto.VerifyCodeDto;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Review;
import pt.feup.les.feupfood.model.ScheduleEnum;
import pt.feup.les.feupfood.repository.AssignMenuRepository;
import pt.feup.les.feupfood.repository.EatIntentionRepository;
import pt.feup.les.feupfood.repository.MenuRepository;
import pt.feup.les.feupfood.repository.ReviewRepository;
import pt.feup.les.feupfood.repository.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RestaurantController_RestTemplateIT {

    @LocalServerPort
    int RANDOM_PORT;

    @Autowired
    private TestRestTemplate restTemplate;

    // direct access to client repositories
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EatIntentionRepository eatIntentionRepository;

    @Autowired
    private AssignMenuRepository assignMenuRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private RegisterUserDto restaurantUser;
    private String token;

    public RestaurantController_RestTemplateIT() {
        this.restaurantUser = new RegisterUserDto();

        this.restaurantUser.setEmail("restaurant@mail.com");
        this.restaurantUser.setPassword("secretPassword");
        this.restaurantUser.setConfirmPassword("secretPassword");
        this.restaurantUser.setFullName("ajsbfasjbfas");
        this.restaurantUser.setTerms(true);
    }

    @BeforeAll
    public void setup() {
        this.registerRestaurant();
        this.token = this.authenticateRestaurant().getJwttoken();
    }

    @Test
    void callRestaurantHome() {
        var headers = this.getStandardHeaders();

        var response = this.restTemplate.exchange(
            "/api/restaurant/home",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            response.getBody()
        ).isEqualTo("Hello restaurant owner!");
    }

    @Test
    void restaurantProfileTest() {
        var headers = this.getStandardHeaders();

        var response = this.restTemplate.exchange(
            "/api/restaurant/profile",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            RestaurantProfileDto.class
        );

        Assertions.assertThat(
            response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            response.getBody().getLocation()
        ).isNull();

        // update the profile information
        RestaurantProfileDto updateProfileDto = new RestaurantProfileDto();
        updateProfileDto.setFullName(response.getBody().getFullName());
        updateProfileDto.setLocation("I do not really know");
        updateProfileDto.setMorningOpeningSchedule(
            Time.valueOf("1:10:10")
        );

        var httpEntity = new HttpEntity<>(updateProfileDto, headers);
        
        
        var responseAfterUpdate = this.restTemplate.exchange(
            "/api/restaurant/profile",
            HttpMethod.PUT,
            httpEntity,
            RestaurantProfileDto.class
        );

        Assertions.assertThat(
            responseAfterUpdate.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        // check that the information was updated
        var getResponseAfterUpdate = this.restTemplate.exchange(
            "/api/restaurant/profile",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            RestaurantProfileDto.class
        );

        Assertions.assertThat(
            getResponseAfterUpdate.getBody().getLocation()
        ).isEqualTo(updateProfileDto.getLocation());

    }

    @Test
    void addAndGetMealTest() {
        var headers = this.getStandardHeaders();

        // get meals that already existed in database
        var previousMealNumber = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMealDto[].class
        ).getBody().length;

        var mealDto = new AddMealDto();
        mealDto.setDescription("Spagheti with atum");
        mealDto.setMealType(MealTypeEnum.FISH);
        mealDto.setNutritionalInformation("Tuna is very healthy.");

        var mealDto1 = new AddMealDto();
        mealDto1.setDescription("Rice with atum and brocoli");
        mealDto1.setMealType(MealTypeEnum.FISH);
        mealDto1.setNutritionalInformation("Tuna is very healthy.");

        var mealDto2 = new AddMealDto();
        mealDto2.setDescription("Francesinha");
        mealDto2.setMealType(MealTypeEnum.MEAT);
        mealDto2.setNutritionalInformation("Francesinha is not that healthy, but it heals the soul.");

        var responseMeal1 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto, headers),
            GetPutMealDto.class
        );

        Assertions.assertThat(
            responseMeal1.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var responseMeal2 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto1, headers),
            GetPutMealDto.class
        );

        var responseMeal3 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto2, headers),
            GetPutMealDto.class
        );

        var getMeals = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMealDto[].class
        );

        Assertions.assertThat(
            getMeals.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getMeals.getBody()
        ).hasSize(previousMealNumber + 3).contains(responseMeal1.getBody(), responseMeal2.getBody(), responseMeal3.getBody());

        mealDto.setDescription("A very different description");

        var updateMeal = this.restTemplate.exchange(
            "/api/restaurant/meal/" + responseMeal1.getBody().getId(),
            HttpMethod.PUT,
            new HttpEntity<>(mealDto, headers),
            GetPutMealDto.class
        );

        Assertions.assertThat(
            updateMeal.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var getMeal = this.restTemplate.exchange(
            "/api/restaurant/meal/" + responseMeal1.getBody().getId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMealDto.class
        );

        Assertions.assertThat(
            getMeal.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getMeal.getBody().getDescription()
        ).isEqualTo(mealDto.getDescription());

        var deleteMeal = this.restTemplate.exchange(
            "/api/restaurant/meal/" + responseMeal1.getBody().getId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            deleteMeal.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var getMealsAfterDelete = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMealDto[].class
        );

        Assertions.assertThat(
            getMealsAfterDelete.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getMealsAfterDelete.getBody()
        ).hasSize(previousMealNumber + 2).contains(responseMeal2.getBody(), responseMeal3.getBody())
                    .doesNotContain(responseMeal1.getBody());
    }

    @Test
    void addAndGetMenuTest() {
        var headers = this.getStandardHeaders();

        var currentMenus = this.restTemplate.exchange(
            "/api/restaurant/menu",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMealDto[].class
        ).getBody().length;

        var mealDto = new AddMealDto();
        mealDto.setDescription("Spagheti with atum");
        mealDto.setMealType(MealTypeEnum.FISH);
        mealDto.setNutritionalInformation("Tuna is very healthy.");

        var meal1 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto, headers),
            GetPutMealDto.class
        );

        var mealDto2 = new AddMealDto();
        mealDto2.setDescription("Rice with carrots");
        mealDto2.setMealType(MealTypeEnum.VEGETARIAN);
        mealDto2.setNutritionalInformation("Tuna is very healthy.");

        var meal2 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto2, headers),
            GetPutMealDto.class
        );

        var mealDto3 = new AddMealDto();
        mealDto3.setDescription("Chocolat mousse");
        mealDto3.setMealType(MealTypeEnum.DESERT);
        mealDto3.setNutritionalInformation("Mhummm mhumm");

        var meal3 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto3, headers),
            GetPutMealDto.class
        );

        var menuDto = new AddMenuDto();
        menuDto.setAdditionalInformation("something else");
        menuDto.setEndPrice(10.0);
        menuDto.setName("Monday morning for this week");
        menuDto.setStartPrice(4.5);
        menuDto.setDiscount(0.30);
        
        var response = this.restTemplate.exchange(
            "/api/restaurant/menu",
            HttpMethod.POST,
            new HttpEntity<>(menuDto, headers),
            GetPutMenuDto.class
        );

        Assertions.assertThat(
            response.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var getResponse = this.restTemplate.exchange(
            "/api/restaurant/menu/" + response.getBody().getId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMenuDto.class
        );

        Assertions.assertThat(
            getResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getResponse
        ).extracting(ResponseEntity::getBody)
            .extracting(GetPutMenuDto::getDesertMeal)
            .isNull();

        // check that there is one more menu
        var getMenus = this.restTemplate.exchange(
            "/api/restaurant/menu",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMenuDto[].class
        );

        Assertions.assertThat(
            getMenus.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getMenus.getBody()
        ).hasSize(currentMenus + 1).contains(response.getBody());

        // update menu with desert and change one of the previous ones
        var newFishMeal = new AddMealDto();
        newFishMeal.setDescription("Sardine with potatoes");
        newFishMeal.setMealType(MealTypeEnum.FISH);
        newFishMeal.setNutritionalInformation("Good for your health");

        var newMeal = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(newFishMeal, headers),
            GetPutMealDto.class
        );

        menuDto.setFishMeal(meal1.getBody().getId());
        menuDto.setDesertMeal(meal3.getBody().getId());
        menuDto.setVegetarianMeal(meal2.getBody().getId());
        menuDto.setMeatMeal(newMeal.getBody().getId());
        menuDto.setDietMeal(meal2.getBody().getId());

        var updateResponse = this.restTemplate.exchange(
            "/api/restaurant/menu/" + response.getBody().getId(),
            HttpMethod.PUT,
            new HttpEntity<>(menuDto, headers),
            GetPutMenuDto.class
        );
        
        Assertions.assertThat(
            updateResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            updateResponse.getBody()
        ).extracting(GetPutMenuDto::getFishMeal).isEqualTo(meal1.getBody());

        Assertions.assertThat(
            updateResponse.getBody()
        ).extracting(GetPutMenuDto::getDesertMeal).isEqualTo(meal3.getBody());

        menuDto.setAdditionalInformation("additionalInformation but different");
        var updateResponse2 = this.restTemplate.exchange(
            "/api/restaurant/menu/" + response.getBody().getId(),
            HttpMethod.PUT,
            new HttpEntity<>(menuDto, headers),
            GetPutMenuDto.class
        );
        
        Assertions.assertThat(
            updateResponse2.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        // delete menu
        var deleteResponse = this.restTemplate.exchange(
            "/api/restaurant/menu/" + updateResponse.getBody().getId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            deleteResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var menusAfterDelete = this.restTemplate.exchange(
            "/api/restaurant/menu",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetPutMenuDto[].class
        );

        Assertions.assertThat(
            menusAfterDelete.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            menusAfterDelete.getBody()
        ).hasSize(currentMenus).doesNotContain(updateResponse.getBody());
    }

    @Test
    void addAndGetAssignmentTest() {
        var headers = this.getStandardHeaders();

        var currentAssignemnts = this.restTemplate.exchange(
            "/api/restaurant/assignment",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto[].class
        ).getBody().length;

        var mealDto = new AddMealDto();
        mealDto.setDescription("Spagheti with atum");
        mealDto.setMealType(MealTypeEnum.FISH);
        mealDto.setNutritionalInformation("Tuna is very healthy.");

        var meal1 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto, headers),
            GetPutMealDto.class
        );

        var mealDto2 = new AddMealDto();
        mealDto2.setDescription("Rice with carrots");
        mealDto2.setMealType(MealTypeEnum.VEGETARIAN);
        mealDto2.setNutritionalInformation("Tuna is very healthy.");

        var meal2 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto2, headers),
            GetPutMealDto.class
        );

        var menuDto = new AddMenuDto();
        menuDto.setAdditionalInformation("something else");
        menuDto.setDietMeal(meal1.getBody().getId());
        menuDto.setEndPrice(10.0);
        menuDto.setFishMeal(meal2.getBody().getId());
        menuDto.setMeatMeal(meal1.getBody().getId());
        menuDto.setName("Monday morning for this week");
        menuDto.setStartPrice(4.5);
        menuDto.setDiscount(0.35);
        menuDto.setVegetarianMeal(meal2.getBody().getId());
        
        var response = this.restTemplate.exchange(
            "/api/restaurant/menu",
            HttpMethod.POST,
            new HttpEntity<>(menuDto, headers),
            GetPutMenuDto.class
        );

        var menuDto2 = new AddMenuDto();
        menuDto2.setAdditionalInformation("something else else");
        menuDto2.setDietMeal(meal1.getBody().getId());
        menuDto2.setEndPrice(10.0);
        menuDto2.setFishMeal(meal2.getBody().getId());
        menuDto2.setMeatMeal(meal2.getBody().getId());
        menuDto2.setName("Monday morning");
        menuDto2.setStartPrice(4.5);
        menuDto2.setDiscount(0.40);
        menuDto2.setVegetarianMeal(meal1.getBody().getId());
        
        var response2 = this.restTemplate.exchange(
            "/api/restaurant/menu",
            HttpMethod.POST,
            new HttpEntity<>(menuDto2, headers),
            GetPutMenuDto.class
        );

        var assignemntDto = new AddAssignmentDto();
        assignemntDto.setDate(
            new Date(1639094400000L)
        );

        assignemntDto.setMenu(response.getBody().getId());
        assignemntDto.setSchedule(ScheduleEnum.LUNCH);

        var createAssignment = this.restTemplate.exchange(
            "/api/restaurant/assignment",
            HttpMethod.POST,
            new HttpEntity<>(assignemntDto, headers),
            GetAssignmentDto.class
        );

        Assertions.assertThat(
            createAssignment.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        // update assignment
        assignemntDto.setSchedule(ScheduleEnum.DINNER);
        assignemntDto.setMenu(response2.getBody().getId());
        var updateAssignment = this.restTemplate.exchange(
            "/api/restaurant/assignment/" + createAssignment.getBody().getId(),
            HttpMethod.PUT,
            new HttpEntity<>(assignemntDto, headers),
            GetAssignmentDto.class
        );

        Assertions.assertThat(
            updateAssignment.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            updateAssignment.getBody()
        ).extracting(GetAssignmentDto::getSchedule).isEqualTo(ScheduleEnum.DINNER);

        // get assignments
        var getAssignments = this.restTemplate.exchange(
            "/api/restaurant/assignment",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto[].class
        );

        Assertions.assertThat(
            getAssignments.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getAssignments.getBody()
        ).hasSize(currentAssignemnts + 1);

        // delete assignment
        var deleteAssignment = this.restTemplate.exchange(
            "/api/restaurant/assignment/" + updateAssignment.getBody().getId(),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            deleteAssignment.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        // add assignment to retrieve it on the next 5 days endpoint
        var createAssignmentNowDto = new AddAssignmentDto();
        createAssignmentNowDto.setDate(new Date(System.currentTimeMillis()));

        long msPerDay = 86400L * 1000;
        
        createAssignmentNowDto.setDate(
            new Date(
                System.currentTimeMillis() + (2 * msPerDay)
            )
        );

        createAssignmentNowDto.setMenu(response2.getBody().getId());
        createAssignmentNowDto.setSchedule(ScheduleEnum.LUNCH);

        var createAssignmentNow = this.restTemplate.exchange(
            "/api/restaurant/assignment",
            HttpMethod.POST,
            new HttpEntity<>(createAssignmentNowDto, headers),
            GetAssignmentDto.class
        );

        Assertions.assertThat(
            createAssignmentNow.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        // create user assignment
        DAOUser client = new DAOUser();
        client.setBiography("biography");
        client.setTerms(true);
        client.setFullName("jose constancionancio");
        client.setPassword("passwordultrasecreta");
        client.setRole("ROLE_USER_CLIENT");
        client.setEmail("josethastreet@mail.com");
        
        client = this.userRepository.save(client);

        var eatIntention = new EatIntention();
        eatIntention.setClient(client);
        
        AssignMenu assignmentFromRepo = this.assignMenuRepository.findById(createAssignmentNow.getBody().getId()).orElseThrow(
            () -> new ResourceNotFoundException("Not found assignment with id: " + createAssignmentNow.getBody().getId())
        );
        eatIntention.setAssignment(assignmentFromRepo);
        eatIntention.setMeals(
            assignmentFromRepo.getMenu().getMeals().stream().filter(
                meal -> meal.getMealType() == MealTypeEnum.FISH
            ).collect(Collectors.toSet())
        );
        eatIntention.setCode("complicatedCode");
        eatIntention.setValidatedCode(false);

        this.eatIntentionRepository.save(eatIntention);

        var getAssignmentsNext5Days = this.restTemplate.exchange(
            "/api/restaurant/assignment/days/5",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto[].class
        );

        Assertions.assertThat(
            getAssignmentsNext5Days.getStatusCode()
        ).isEqualTo(HttpStatus.OK);
        
        Assertions.assertThat(
            getAssignmentsNext5Days.getBody()
        ).hasSize(1).extracting(GetAssignmentDto::getNumberOfIntentions)
            .containsOnly(1);

        // test verification of the code
        var verificationCodeResponse = this.restTemplate.exchange(
            "/api/restaurant/assignment/" + createAssignmentNow.getBody().getId() + 
            "/verify-code/" + eatIntention.getCode(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            VerifyCodeDto.class
        );

        Assertions.assertThat(
            verificationCodeResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            verificationCodeResponse.getBody()
        ).extracting(VerifyCodeDto::getFullName)
            .isEqualTo(eatIntention.getClient().getFullName());

        // test a bad code
        var badVerificationCodeResponse = this.restTemplate.exchange(
            "/api/restaurant/assignment/" + createAssignmentNow.getBody().getId() + 
            "/verify-code/" + "blablabla i dont exist",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            VerifyCodeDto.class
        );

        Assertions.assertThat(
            badVerificationCodeResponse.getStatusCode()
        ).isEqualTo(HttpStatus.NOT_ACCEPTABLE);

        // test a code already accepted
        var repeatedVerificationCodeResponse = this.restTemplate.exchange(
            "/api/restaurant/assignment/" + createAssignmentNow.getBody().getId() + 
            "/verify-code/" + eatIntention.getCode(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            VerifyCodeDto.class
        );

        Assertions.assertThat(
            repeatedVerificationCodeResponse.getStatusCode()
        ).isEqualTo(HttpStatus.NOT_ACCEPTABLE);

        // test automatic verification of code
        // create an assignment for today

        AssignMenu assignmentForToday = new AssignMenu();
        Menu menuForToday = this.menuRepository.findById(
                response.getBody().getId()
            ).orElseThrow(
                () -> new ResourceNotFoundException("Menu was not found")
            );

        assignmentForToday.setMenu(menuForToday);
        Calendar now = Calendar.getInstance();
        assignmentForToday.setDate(now.getTime());
        assignmentForToday.setRestaurant(menuForToday.getRestaurant());

        if (now.get(Calendar.HOUR_OF_DAY) > 16)
            assignmentForToday.setSchedule(ScheduleEnum.DINNER);
        else
            assignmentForToday.setSchedule(ScheduleEnum.LUNCH);

        assignmentForToday = this.assignMenuRepository.save(assignmentForToday);

        AssignMenu assignmentForTomorrow = new AssignMenu();
        assignmentForTomorrow.setMenu(menuForToday);
        assignmentForTomorrow.setDate(new Date(now.getTimeInMillis() + 86400001));
        assignmentForTomorrow.setRestaurant(menuForToday.getRestaurant());
        assignmentForTomorrow.setSchedule(ScheduleEnum.DINNER);

        assignmentForTomorrow = this.assignMenuRepository.save(assignmentForTomorrow);
        
        EatIntention eatIntentionForToday = new EatIntention();
        eatIntentionForToday.setAssignment(assignmentForToday);
        eatIntentionForToday.setMeals(
            menuForToday.getMeals().stream().filter(
                meal -> meal.getMealType() == MealTypeEnum.FISH
            ).collect(Collectors.toSet())
        );
        String codeForToday = "justanothercode";
        eatIntentionForToday.setCode(codeForToday);
        eatIntentionForToday.setValidatedCode(false);
        eatIntentionForToday.setClient(client);

        this.eatIntentionRepository.save(eatIntentionForToday);

        var verifyCodeAutomaticallyResponse = this.restTemplate.exchange(
            "/api/restaurant/assignment/verify-code/" + codeForToday,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            VerifyCodeDto.class
        );

        Assertions.assertThat(
            verifyCodeAutomaticallyResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        // verify the response when we try to get 0 days
        var getAssignmentsForToday = this.restTemplate.exchange(
            "/api/restaurant/assignment/days/0",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto[].class
        );

        Assertions.assertThat(
            getAssignmentsForToday.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getAssignmentsForToday.getBody()
        ).hasSize(1).extracting(GetAssignmentDto::getId)
            .contains(assignmentForToday.getId())
            .doesNotContain(assignmentForTomorrow.getId());

        var getCurrentAssignment = this.restTemplate.exchange(
            "/api/restaurant/assignment/now",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto.class
        );

        Assertions.assertThat(
            getCurrentAssignment.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

    }

    @Test
    void answerToReviewsTest() {
        // make some reviews
        HttpHeaders headers = this.getStandardHeaders();

        DAOUser client = this.userRepository.findByEmail("francisco@gmail.com").orElseThrow();

        DAOUser restaurant = this.userRepository.findByEmail(this.restaurantUser.getEmail()).orElseThrow();

        Review review1 = new Review();
        review1.setClassificationGrade(3);
        review1.setClient(client);
        review1.setComment("Eaten better.");
        review1.setRestaurant(restaurant.getRestaurant());
        review1.setTimestamp(new Timestamp(System.currentTimeMillis()));
        
        Review review2 = new Review();
        review2.setClassificationGrade(4);
        review2.setClient(client);
        review2.setComment("Today the food was better");
        review2.setRestaurant(restaurant.getRestaurant());
        review2.setTimestamp(new Timestamp(System.currentTimeMillis()));

        review1 = this.reviewRepository.save(review1);
        review2 = this.reviewRepository.save(review2);

        var getReviewsOfRestaurant = this.restTemplate.exchange(
            "/api/restaurant/review",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientReviewDto[].class
        );

        Assertions.assertThat(
            getReviewsOfRestaurant.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getReviewsOfRestaurant.getBody()
        ).extracting(GetClientReviewDto::getId).contains(
            review1.getId(),
            review2.getId()
        );

        RestaurantAnswerReviewDto answerDto = new RestaurantAnswerReviewDto();
        answerDto.setAnswer("Where have you eaten better? Tell me now!");

        var addAnswerToComment = this.restTemplate.exchange(
            "/api/restaurant/review/answer/" + review1.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(answerDto, headers),
            GetClientReviewDto.class
        );

        Assertions.assertThat(
            addAnswerToComment.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            addAnswerToComment.getBody().getAnswer()
        ).isEqualTo(answerDto.getAnswer());

        answerDto.setAnswer("Can you please tell where have you eaten better? So that we can improve.");
        var updateAnswerToComment = this.restTemplate.exchange(
            "/api/restaurant/review/" + review1.getId(),
            HttpMethod.PUT,
            new HttpEntity<>(answerDto, headers),
            GetClientReviewDto.class
        );

        Assertions.assertThat(
            updateAnswerToComment.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            updateAnswerToComment.getBody().getAnswer()
        ).isEqualTo(answerDto.getAnswer());

        var getReviewWithId = this.restTemplate.exchange(
            "/api/restaurant/review/" + review2.getId(),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientReviewDto.class
        );

        Assertions.assertThat(
            getReviewWithId.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getReviewWithId.getBody().getId()
        ).isEqualTo(review2.getId());

        // access one review that does not exist
        var accessignReviewNotExistent = this.restTemplate.exchange(
            "/api/restaurant/review/" + 101L,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientReviewDto.class
        );

        Assertions.assertThat(
            accessignReviewNotExistent.getStatusCode()
        ).isEqualTo(HttpStatus.NOT_FOUND);

        // answer to review not owned
        var answeringNotOwnedReview = this.restTemplate.exchange(
            "/api/restaurant/review/" + 1L,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetClientReviewDto.class
        );

        Assertions.assertThat(
            answeringNotOwnedReview.getStatusCode()
        ).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void statsTest() {
        HttpHeaders headers = this.getStandardHeaders();

        // create clients
        DAOUser client1 = new DAOUser();
        client1.setBiography("different biography");
        client1.setTerms(true);
        client1.setFullName("Antonio Alves");
        client1.setPassword("passwordMuitoSecreta");
        client1.setRole("ROLE_USER_CLIENT");
        client1.setEmail("idontknowme@mail.com");
        
        DAOUser client2 = new DAOUser();
        client2.setBiography("another biography");
        client2.setTerms(true);
        client2.setFullName("Maria Fernanda");
        client2.setPassword("passwordMuitoSecreta2");
        client2.setRole("ROLE_USER_CLIENT");
        client2.setEmail("nandinha@mail.com");

        client1 = this.userRepository.save(client1);
        client2 = this.userRepository.save(client2);

        DAOUser restaurant = this.userRepository.findByEmail(this.restaurantUser.getEmail()).orElseThrow();

        // add meals
        var mealDto = new AddMealDto();
        mealDto.setDescription("Spagheti with atum");
        mealDto.setMealType(MealTypeEnum.FISH);
        mealDto.setNutritionalInformation("Tuna is very healthy.");

        var meal1 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto, headers),
            GetPutMealDto.class
        );

        Assertions.assertThat(
            meal1.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var mealDto2 = new AddMealDto();
        mealDto2.setDescription("Rice with carrots");
        mealDto2.setMealType(MealTypeEnum.VEGETARIAN);
        mealDto2.setNutritionalInformation("Tuna is very healthy.");

        var meal2 = this.restTemplate.exchange(
            "/api/restaurant/meal",
            HttpMethod.POST,
            new HttpEntity<>(mealDto2, headers),
            GetPutMealDto.class
        );

        Assertions.assertThat(
            meal2.getStatusCode()
        ).isEqualTo(HttpStatus.OK);
        // add menu
        var menuDto = new AddMenuDto();
        menuDto.setAdditionalInformation("something else");
        menuDto.setEndPrice(10.0);
        menuDto.setName("Monday morning for this week");
        menuDto.setStartPrice(4.5);
        menuDto.setDiscount(0.30);

        menuDto.setFishMeal(meal1.getBody().getId());
        menuDto.setDesertMeal(meal2.getBody().getId());
        menuDto.setVegetarianMeal(meal2.getBody().getId());
        menuDto.setMeatMeal(meal1.getBody().getId());
        menuDto.setDietMeal(meal2.getBody().getId());
        
        var menuResponse = this.restTemplate.exchange(
            "/api/restaurant/menu",
            HttpMethod.POST,
            new HttpEntity<>(menuDto, headers),
            GetPutMenuDto.class
        );

        Assertions.assertThat(
            menuResponse.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Menu menu = this.menuRepository.findFirstByRestaurant(restaurant.getRestaurant(), Sort.by(Direction.DESC, "startPrice"));

        long firstDayOfTheYear = 1640995200000L;
        long oneDay = 1000L * 60 * 60 * 24;
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

        // in the first day of the year both eaten on dinner and lunch
        EatIntention firstDayIntentionLunch1 = new EatIntention();
        firstDayIntentionLunch1.setAssignment(assignment1);
        firstDayIntentionLunch1.setClient(client1);
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
        firstDayIntentionDinner1.setClient(client1);
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
        secondDayIntentionDinner.setClient(client1);
        secondDayIntentionDinner.setCode("123456784");
        secondDayIntentionDinner.setMeals(Set.of(menu.getMeals().get(0)));
        secondDayIntentionDinner.setValidatedCode(true);

        // on the third day both dinner at lunch
        EatIntention thirdDayLunch1 = new EatIntention();
        thirdDayLunch1.setAssignment(assignment5);
        thirdDayLunch1.setClient(client1);
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
        thirdDayDinner.setClient(client1);
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
        reviewFirstDay.setClient(client1);
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
        reviewSecondDay.setClient(client1);
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
        reviewThirdDay.setClient(client1);
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

        ParameterizedTypeReference<HashMap<Date, StatsIntentionDto>> statIntentionResponse = new ParameterizedTypeReference<HashMap<Date, StatsIntentionDto>>() {};

        var getIntentionStats = this.restTemplate.exchange(
            "/api/restaurant/stats/intention/1/Jan 01 2022/Jan 03 2022",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            statIntentionResponse
        );

        Assertions.assertThat(
            getIntentionStats.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        var getIntentionErrorEndDateInferiorToStartDate = this.restTemplate.exchange(
            "/api/restaurant/stats/intention/1/Jan 02 2022/Jan 01 2022",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            getIntentionErrorEndDateInferiorToStartDate.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

        // test get clients that favorited the restaurant
        client1.addFavoriteRestaurant(restaurant.getRestaurant());
        client2.addFavoriteRestaurant(restaurant.getRestaurant());

        this.userRepository.save(client1);
        this.userRepository.save(client2);
    
        var getNumberOfFavoritedClients = this.restTemplate.exchange(
            "/api/restaurant/stats/favorite",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            Integer.class
        );

        Assertions.assertThat(
            getNumberOfFavoritedClients.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getNumberOfFavoritedClients.getBody()
        ).isEqualTo(2);

        ParameterizedTypeReference<HashMap<Date, Float>> popularityResponse = new ParameterizedTypeReference<HashMap<Date, Float>>() {};

        var getPopularity = this.restTemplate.exchange(
            "/api/restaurant/stats/popularity/1/Jan 01 2022/Jan 05 2022",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            popularityResponse
        );

        Assertions.assertThat(
            getPopularity.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            getPopularity.getBody().values()
        ).contains(5.0f, 4.75f);

        var getPopularityErrorEndDateInferiorToStartDate = this.restTemplate.exchange(
            "/api/restaurant/stats/popularity/1/Jan 02 2022/Jan 01 2022",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            getPopularityErrorEndDateInferiorToStartDate.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

        var getMostEatenMeals = this.restTemplate.exchange(
            "/api/restaurant/stats/favorite-meals/3",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        Assertions.assertThat(
            getMostEatenMeals.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

    }

    private void registerRestaurant() {
        this.restTemplate.postForEntity("/api/restaurant/register",
                this.restaurantUser,
                RegisterUserResponseDto.class);
    }

    private JwtResponse authenticateRestaurant() {
        var jwtRequest = new JwtRequest();
        jwtRequest.setEmail(this.restaurantUser.getEmail());
        jwtRequest.setPassword(this.restaurantUser.getPassword());

        return this.restTemplate.postForEntity(
            "/api/auth/sign-in",
            jwtRequest,
            JwtResponse.class
        ).getBody();
    }
    
    private HttpHeaders getStandardHeaders() {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + this.token);
        return headers;
    }
}