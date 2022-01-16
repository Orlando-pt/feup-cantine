package pt.feup.les.feupfood.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.Principal;
import java.sql.Time;
import java.time.Clock;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.exceptions.DataIntegrityException;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.exceptions.ResourceNotOwnedException;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.ScheduleEnum;
import pt.feup.les.feupfood.repository.AssignMenuRepository;
import pt.feup.les.feupfood.repository.MealRepository;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private AssignMenuRepository assignMenuRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private RestaurantService service;

    private DAOUser owner1;
    private DAOUser owner2;
    private Restaurant restaurant1;
    private Restaurant restaurant2;

    // auxiliar resources
    private Principal user;
    private RestaurantProfileDto profileDto;

    private Meal meal1;
    private AddMealDto mealDto;
    private GetPutMealDto mealDtoResponse;

    private Menu menu;
    private AssignMenu assignment;
    private AssignMenu assignment2;

    private Calendar now;

    @BeforeEach
    void setup() {
        this.generateData();
    }

    @Test
    void testThereAreNoAssignments() {
        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.clock.millis()).thenReturn(
            this.now.getTimeInMillis()
        );

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        assertThrows(
            ResourceNotFoundException.class,
            () -> this.service.verifyCodeAutomatically(
                this.user,
                "blabla"
            )
        );
    }

    @Test
    void testWhenThereIsOneAssignment() {

        String code = "blablabla";
        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.clock.millis()).thenReturn(
            this.now.getTimeInMillis()
        );

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        Mockito.when(this.assignMenuRepository.findByDateAndRestaurant(
            Mockito.any(),
            Mockito.eq(this.restaurant1)
        )).thenReturn(Arrays.asList(this.assignment));

        assertThrows(ResourceNotFoundException.class, () -> this.service.verifyCodeAutomatically(user, code));

        Mockito.verify(this.assignMenuRepository, Mockito.times(1)).findById(this.assignment.getId());
    }

    @Test
    void testWhenThereAreMoreThanTwoAssignments() {

        String code = "blablabla";
        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.clock.millis()).thenReturn(
            this.now.getTimeInMillis()
        );

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        Mockito.when(this.assignMenuRepository.findByDateAndRestaurant(
            Mockito.any(),
            Mockito.eq(this.restaurant1)
        )).thenReturn(Arrays.asList(this.assignment, this.assignment, this.assignment));

        assertThrows(DataIntegrityException.class, () -> this.service.verifyCodeAutomatically(user, code));
    }

    @Test
    void testWhenThereAreTwoAssignments_lunch() {

        String code = "blablabla";
        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.clock.millis()).thenReturn(
            this.now.getTimeInMillis()
        );

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        this.now.set(Calendar.HOUR_OF_DAY, 21);

        Mockito.when(this.clock.millis()).thenReturn(this.now.getTimeInMillis());

        Mockito.when(
            this.assignMenuRepository.findByDateAndRestaurant(
                Mockito.any(),
                Mockito.eq(this.restaurant1)
            )
        ).thenReturn(Arrays.asList(this.assignment, this.assignment2));

        assertThrows(ResourceNotFoundException.class, () -> this.service.verifyCodeAutomatically(user, code));

        Mockito.verify(this.assignMenuRepository, Mockito.times(1)).findById(this.assignment2.getId());
    }

    @Test
    void testWhenThereAreTwoAssignments_dinner() {

        String code = "blablabla";
        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.clock.millis()).thenReturn(
            this.now.getTimeInMillis()
        );

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        this.now.set(Calendar.HOUR_OF_DAY, 12);

        Mockito.when(this.clock.millis()).thenReturn(this.now.getTimeInMillis());

        Mockito.when(
            this.assignMenuRepository.findByDateAndRestaurant(
                Mockito.any(),
                Mockito.eq(this.restaurant1)
            )
        ).thenReturn(Arrays.asList(this.assignment, this.assignment2));

        assertThrows(ResourceNotFoundException.class, () -> this.service.verifyCodeAutomatically(user, code));

        Mockito.verify(this.assignMenuRepository, Mockito.times(1)).findById(this.assignment.getId());
    }
    @Test
    void testUpdateRestaurantProfileAllProcessWithoutProblems() {
        this.commonUpdateProfileData();

        Mockito.when(
            this.userRepository.save(Mockito.any())
        ).thenReturn(null);

        Mockito.when(
            this.restaurantRepository.save(Mockito.any())
        ).thenReturn(null);
        
        ResponseEntity<?> answer = this.service.updateRestaurantProfile(
            this.user,
            this.profileDto
            );
        
        this.owner1.setFullName(profileDto.getFullName());
        Mockito.verify(this.userRepository, Mockito.times(1)).save(this.owner1);

        this.restaurant1.setLocation(profileDto.getLocation());
        Mockito.verify(this.restaurantRepository, Mockito.times(1)).save(this.restaurant1);

        Assertions.assertThat(
            answer.getStatusCode()
        ).isEqualTo(
            HttpStatus.OK
        );

        Assertions.assertThat(
            answer.getBody()
        ).isEqualTo(profileDto);
    }

    @Test
    void testUpdateRestaurantProfileSaveOwnerRetrievesError() {
        this.commonUpdateProfileData();

        Mockito.when(
            this.userRepository.save(Mockito.any())
        ).thenThrow(new PersistenceException("Full name is incorrect"));

        ResponseEntity<ResponseInterfaceDto> answer = this.service.updateRestaurantProfile(
            this.user,
            this.profileDto
            );
        
        Assertions.assertThat(
            answer.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

        Assertions.assertThat(
            answer.getBody()
        ).isInstanceOf(ResponseInterfaceDto.class);
    }

    @Test
    void testUpdateRestaurantProfileSaveRestaurantRetrievesError() {
        this.commonUpdateProfileData();

        Mockito.when(
            this.userRepository.save(Mockito.any())
        ).thenReturn(null);

        Mockito.when(
            this.restaurantRepository.save(Mockito.any())
        ).thenThrow(new PersistenceException("The restaurant profile data submited was badly written."));

        ResponseEntity<ResponseInterfaceDto> answer = this.service.updateRestaurantProfile(
            this.user,
            this.profileDto
            );
        
        Assertions.assertThat(
            answer.getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

        Assertions.assertThat(
            answer.getBody()
        ).isInstanceOf(ResponseInterfaceDto.class);
    }

    @Test
    void addMealTest() {
        this.profileDto = new RestaurantProfileDto();
        this.profileDto.setFullName("new Full Name");
        this.profileDto.setLocation("on the other corner");
        this.profileDto.setMorningOpeningSchedule(this.restaurant1.getMorningOpeningSchedule());
        this.profileDto.setAfternoonClosingSchedule(this.restaurant1.getAfternoonClosingSchedule());

        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        Meal mealSaved = new Meal();
        mealSaved.setDescription(this.meal1.getDescription());
        mealSaved.setMealType(this.meal1.getMealType());
        mealSaved.setNutritionalInformation(this.meal1.getNutritionalInformation());
        mealSaved.setRestaurant(this.restaurant1);

        Mockito.when(this.mealRepository.save(mealSaved)).thenReturn(
            this.meal1
        );

        Mockito.when(this.restaurantRepository.save(Mockito.any())).thenReturn(
            null
        );

        Assertions.assertThat(
            this.service.addMeal(this.user, this.mealDto).getBody()
        ).isEqualTo(this.mealDtoResponse);

        Mockito.verify(this.mealRepository, Mockito.times(1)).save(mealSaved);

    }

    @Test
    void addMealTestThrowExceptionOnBadMeal() {
        this.profileDto = new RestaurantProfileDto();
        this.profileDto.setFullName("new Full Name");
        this.profileDto.setLocation("on the other corner");
        this.profileDto.setMorningOpeningSchedule(this.restaurant1.getMorningOpeningSchedule());
        this.profileDto.setAfternoonClosingSchedule(this.restaurant1.getAfternoonClosingSchedule());

        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        Meal mealSaved = new Meal();
        mealSaved.setDescription(this.meal1.getDescription());
        mealSaved.setMealType(this.meal1.getMealType());
        mealSaved.setNutritionalInformation(this.meal1.getNutritionalInformation());
        mealSaved.setRestaurant(this.restaurant1);

        Mockito.when(this.mealRepository.save(mealSaved)).thenThrow(
            new PersistenceException("Upsi")
        );

        Assertions.assertThat(
            this.service.addMeal(this.user, this.mealDto).getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

        Mockito.verify(this.mealRepository, Mockito.times(1)).save(mealSaved);

    }

    @Test
    void addMealTestThrowExceptionOnBadRestaurantSave() {
        this.profileDto = new RestaurantProfileDto();
        this.profileDto.setFullName("new Full Name");
        this.profileDto.setLocation("on the other corner");
        this.profileDto.setMorningOpeningSchedule(this.restaurant1.getMorningOpeningSchedule());
        this.profileDto.setAfternoonClosingSchedule(this.restaurant1.getAfternoonClosingSchedule());

        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        Meal mealSaved = new Meal();
        mealSaved.setDescription(this.meal1.getDescription());
        mealSaved.setMealType(this.meal1.getMealType());
        mealSaved.setNutritionalInformation(this.meal1.getNutritionalInformation());
        mealSaved.setRestaurant(this.restaurant1);

        Mockito.when(this.mealRepository.save(mealSaved)).thenReturn(
            this.meal1
        );

        Mockito.when(
            this.restaurantRepository.save(Mockito.any())
        ).thenThrow(new PersistenceException("The restaurant profile data submited was badly written."));

        Assertions.assertThat(
            this.service.addMeal(this.user, this.mealDto).getStatusCode()
        ).isEqualTo(HttpStatus.BAD_REQUEST);

        Mockito.verify(this.mealRepository, Mockito.times(1)).save(mealSaved);
    }

    @Test
    void getMealTestAllOk() {
        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        Mockito.when(this.mealRepository.findById(this.meal1.getId())).thenReturn(
            Optional.of(this.meal1)
        );

        ResponseEntity<ResponseInterfaceDto> answer = this.service.getMeal(
            this.user,
            this.meal1.getId()
        );

        Mockito.verify(this.mealRepository, 
            Mockito.times(1)).findById(this.meal1.getId());

        Assertions.assertThat(
            answer.getStatusCode()
        ).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(
            answer.getBody()
        ).isEqualTo(this.mealDtoResponse);
    }

    @Test
    void getOwnerRetrievesError() {
        String email = "email@mail.com";
        Mockito.when(this.userRepository.findByEmail(email)).thenReturn(
            Optional.ofNullable(null)
        );

        Principal user = Mockito.mock(Principal.class);
        Mockito.when(user.getName()).thenReturn(
            email
        );

        assertThrows(
            UsernameNotFoundException.class,
            () -> this.service.deleteMeal(user, 1L)
        );
    }

    @Test
    void getRestaurantRetrievesUsernameNotFoundException() {
        DAOUser user = new DAOUser();
        user.setEmail("email");
        user.setFullName("fullName");
        user.setPassword("password");

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(
            user.getEmail()
        );

        Mockito.when(
            this.userRepository.findByEmail(user.getEmail())
        ).thenReturn(
            Optional.of(user)
        );

        Mockito.when(
            this.restaurantRepository.findByOwner(user)
        ).thenReturn(Optional.ofNullable(null));

        assertThrows(
            UsernameNotFoundException.class,
            () -> this.service.getRestaurantProfile(principal)
        );

    }

    @Test
    void getMealRetrievesResourceNotFoundException() {
        DAOUser user = new DAOUser();
        user.setEmail("email");
        user.setFullName("fullName");
        user.setPassword("password");

        Long mealId = 10L;

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(
            user.getEmail()
        );

        Mockito.when(
            this.userRepository.findByEmail(user.getEmail())
        ).thenReturn(
            Optional.of(user)
        );

        Mockito.when(
            this.mealRepository.findById(mealId)
        ).thenReturn(Optional.ofNullable(null));

        assertThrows(
            ResourceNotFoundException.class,
            () -> this.service.deleteMeal(principal, mealId)
        );
    }

    @Test
    void getMealRetrievesResourceNotOwnedException() {
        DAOUser user = new DAOUser();
        user.setEmail("email");
        user.setFullName("fullName");
        user.setPassword("password");

        Restaurant restaurant = new Restaurant();
        restaurant.setLocation("location");
        restaurant.setId(1L);
        restaurant.setOwner(user);
        user.setRestaurant(restaurant);

        Restaurant otherRestaurant = new Restaurant();
        otherRestaurant.setLocation("location");
        otherRestaurant.setId(2L);

        Meal meal = new Meal();
        meal.setDescription("description");
        meal.setMealType(MealTypeEnum.DESERT);
        meal.setNutritionalInformation("nutritionalInformation");
        meal.setId(10L);
        meal.setRestaurant(otherRestaurant);

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(
            user.getEmail()
        );

        Mockito.when(
            this.userRepository.findByEmail(user.getEmail())
        ).thenReturn(
            Optional.of(user)
        );

        Mockito.when(
            this.mealRepository.findById(meal.getId())
        ).thenReturn(Optional.ofNullable(meal));

        Long mealId = meal.getId();

        assertThrows(
            ResourceNotOwnedException.class,
            () -> this.service.deleteMeal(principal, mealId)
        );
    }

    @Test
    void getMealTestThrowResourceNotFoundException() {
        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        Mockito.when(this.mealRepository.findById(this.meal1.getId())).thenReturn(
            Optional.ofNullable(null)
        );

        assertThrows(ResourceNotFoundException.class, 
            () -> this.service.getMeal(
                this.user, 100L)
        );

        Mockito.verify(this.mealRepository, 
            Mockito.times(1)).findById(this.meal1.getId());

    }

    @Test
    void getMealTestThrowResourceNotOwnedException() {
        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        this.meal1.setRestaurant(new Restaurant());
        Mockito.when(this.mealRepository.findById(this.meal1.getId())).thenReturn(
            Optional.ofNullable(this.meal1)
        );

        assertThrows(ResourceNotOwnedException.class, 
            () -> this.service.getMeal(
                this.user, 100L)
        );

        Mockito.verify(this.mealRepository, 
            Mockito.times(1)).findById(this.meal1.getId());

    }

    private void commonUpdateProfileData() {
        this.profileDto = new RestaurantProfileDto();
        this.profileDto.setFullName("new Full Name");
        this.profileDto.setLocation("on the other corner");
        this.profileDto.setMorningOpeningSchedule(this.restaurant1.getMorningOpeningSchedule());
        this.profileDto.setAfternoonClosingSchedule(this.restaurant1.getAfternoonClosingSchedule());

        this.user = Mockito.mock(Principal.class);

        Mockito.when(this.user.getName()).thenReturn(
            this.owner1.getEmail()
        );

        Mockito.when(this.userRepository.findByEmail(this.owner1.getEmail())).thenReturn(
            Optional.of(this.owner1)
        );

        Mockito.when(this.restaurantRepository.findByOwner(this.owner1)).thenReturn(
            Optional.of(this.restaurant1)
        );
    }

    private void generateData() {
        this.now = Calendar.getInstance();
        this.now.set(2022, 1, 8);
        this.owner1 = new DAOUser();
        this.owner1.setEmail("email@mail.com");
        this.owner1.setFullName("Maria Antónia");
        this.owner1.setPassword("password");
        this.owner1.setRole("ROLE_USER_RESTAURANT");
        this.owner1.setTerms(true);

        this.restaurant1 = new Restaurant();
        this.restaurant1.setLocation("On the left corner");
        this.restaurant1.setMorningOpeningSchedule(
            Time.valueOf("09:30:00")
        );
        this.restaurant1.setAfternoonOpeningSchedule(
            Time.valueOf("19:30:00")
        );
        this.restaurant1.setOwner(this.owner1);

        this.owner2 = new DAOUser();
        this.owner2.setEmail("email2@mail.com");
        this.owner2.setFullName("Josefina Antunes");
        this.owner2.setPassword("password");
        this.owner2.setRole("ROLE_USER_RESTAURANT");
        this.owner2.setTerms(true);

        this.restaurant2 = new Restaurant();
        this.restaurant2.setLocation("On the right corner");
        this.restaurant2.setMorningOpeningSchedule(
            Time.valueOf("09:30:00")
        );
        this.restaurant2.setAfternoonOpeningSchedule(
            Time.valueOf("19:30:00")
        );
        this.restaurant2.setOwner(this.owner2);

        this.owner1.setRestaurant(this.restaurant1);
        this.owner2.setRestaurant(this.restaurant2);

        this.meal1 = new Meal();
        this.meal1.setDescription("Rotten apple");
        this.meal1.setId(100L);
        this.meal1.setMealType(MealTypeEnum.DESERT);
        this.meal1.setNutritionalInformation("very good for your muscles");
        this.meal1.setRestaurant(this.restaurant1);

        this.mealDto = new AddMealDto();
        this.mealDto.setDescription(this.meal1.getDescription());
        this.mealDto.setMealType(this.meal1.getMealType());
        this.mealDto.setNutritionalInformation(this.meal1.getNutritionalInformation());

        this.mealDtoResponse = new GetPutMealDto();
        this.mealDtoResponse.setDescription(this.meal1.getDescription());
        this.mealDtoResponse.setId(this.meal1.getId());
        this.mealDtoResponse.setMealType(this.meal1.getMealType());
        this.mealDtoResponse.setNutritionalInformation(this.meal1.getNutritionalInformation());

        this.menu = new Menu();
        this.menu.setAdditionalInformation("additionalInformation");
        this.menu.setEndPrice(10.5);
        this.menu.setId(100L);
        this.menu.setName("Monday morning");
        this.menu.setRestaurant(this.restaurant1);
        this.menu.setStartPrice(5.1);
        this.menu.addMeal(this.meal1);

        this.assignment = new AssignMenu();
        this.assignment.setMenu(this.menu);
        this.now.set(Calendar.HOUR_OF_DAY, 13);
        this.assignment.setDate(this.now.getTime());
        this.assignment.setId(50L);
        this.assignment.setSchedule(ScheduleEnum.LUNCH);
        this.assignment.setRestaurant(this.restaurant1);

        this.assignment2 = new AssignMenu();
        this.assignment2.setMenu(this.menu);
        this.now.set(Calendar.HOUR_OF_DAY, 20);
        this.assignment2.setDate(this.now.getTime());
        this.assignment2.setId(51L);
        this.assignment2.setSchedule(ScheduleEnum.DINNER);
        this.assignment2.setRestaurant(this.restaurant1);
    }
}
