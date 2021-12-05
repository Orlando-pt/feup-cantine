package pt.feup.les.feupfood.service;

import java.security.Principal;
import java.sql.Date;
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

import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService service;

    private DAOUser owner1;
    private DAOUser owner2;
    private Restaurant restaurant1;
    private Restaurant restaurant2;

    // auxiliar resources
    private Principal user;
    private RestaurantProfileDto profileDto;

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

    private void commonUpdateProfileData() {
        this.profileDto = new RestaurantProfileDto();
        this.profileDto.setFullName("new Full Name");
        this.profileDto.setLocation("on the other corner");
        this.profileDto.setOpeningSchedule(this.restaurant1.getOpeningSchedule());
        this.profileDto.setClosingSchedule(this.restaurant1.getClosingSchedule());

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
        this.restaurant1.setOpeningSchedule(
            new Date(1638703519L)
        );
        this.restaurant1.setClosingSchedule(
            new Date(1638703819L)
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
        this.restaurant2.setOpeningSchedule(
            new Date(1638703519L)
        );
        this.restaurant2.setClosingSchedule(
            new Date(1638703819L)
        );
        this.restaurant2.setOwner(this.owner2);

        this.owner1.setRestaurant(this.restaurant1);
        this.owner2.setRestaurant(this.restaurant2);
    }
}
