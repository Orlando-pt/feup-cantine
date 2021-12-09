package pt.feup.les.feupfood.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.Principal;
import java.sql.Time;
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

import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.ExceptionResponseDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.exceptions.ResourceNotOwnedException;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;
import pt.feup.les.feupfood.model.Restaurant;
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

    @BeforeEach
    void setup() {
        this.generateData();
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
    }
}
