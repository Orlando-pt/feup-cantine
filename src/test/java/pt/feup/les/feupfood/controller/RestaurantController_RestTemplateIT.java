package pt.feup.les.feupfood.controller;

import java.sql.Time;
import java.util.Date;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pt.feup.les.feupfood.dto.AddAssignmentDto;
import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.AddMenuDto;
import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
import pt.feup.les.feupfood.dto.JwtRequest;
import pt.feup.les.feupfood.dto.JwtResponse;
import pt.feup.les.feupfood.dto.RegisterUserDto;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.ScheduleEnum;
import pt.feup.les.feupfood.repository.AssignMenuRepository;
import pt.feup.les.feupfood.repository.EatIntentionRepository;
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

        long msPerDay = 86400 * 1000;
        long ms = System.currentTimeMillis();
        
        createAssignmentNowDto.setDate(
            new Date(
                ms - (ms % msPerDay)
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
        
        var assignmentFromRepo = this.assignMenuRepository.findById(createAssignmentNow.getBody().getId()).orElseThrow(
            () -> new ResourceNotFoundException("Not found assignment with id: " + createAssignmentNow.getBody().getId())
        );
        eatIntention.setAssignment(assignmentFromRepo);
        eatIntention.setMeals(
            assignmentFromRepo.getMenu().getMeals().stream().filter(
                meal -> meal.getMealType() == MealTypeEnum.FISH
            ).collect(Collectors.toSet())
        );

        this.eatIntentionRepository.save(eatIntention);

        var getAssignmentsNext5Days = this.restTemplate.exchange(
            "/api/restaurant/assignment/days/5",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GetAssignmentDto[].class
        );

        System.out.println(getAssignmentsNext5Days.getBody()[0]);

        Assertions.assertThat(
            getAssignmentsNext5Days.getStatusCode()
        ).isEqualTo(HttpStatus.OK);
        
        Assertions.assertThat(
            getAssignmentsNext5Days.getBody()
        ).hasSize(1).extracting(GetAssignmentDto::getNumberOfIntentions)
            .containsOnly(1);

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