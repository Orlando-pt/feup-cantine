package pt.feup.les.feupfood.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.ExceptionResponseDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.exceptions.ResourceNotOwnedException;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.repository.MealRepository;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.UserRepository;
import pt.feup.les.feupfood.util.RestaurantParser;

@Service
public class RestaurantService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MealRepository mealRepository;

    public ResponseEntity<RestaurantProfileDto> getRestaurantProfile(
        Principal user
    ) {
        RestaurantProfileDto restaurantDto = new RestaurantProfileDto();

        DAOUser owner = this.retrieveRestaurantOwner(user.getName());
        Restaurant restaurant = this.retrieveRestaurant(owner);

        restaurantDto.setFullName(owner.getFullName());
        restaurantDto.setLocation(restaurant.getLocation());
        restaurantDto.setClosingSchedule(restaurant.getClosingSchedule());
        restaurantDto.setOpeningSchedule(restaurant.getOpeningSchedule());
        
        return ResponseEntity.ok(restaurantDto);
    }

    public ResponseEntity<ResponseInterfaceDto> updateRestaurantProfile(
        Principal user,
        RestaurantProfileDto profileDto
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        Restaurant restaurant = this.retrieveRestaurant(
            owner
        );

        owner.setFullName(profileDto.getFullName());

        restaurant.setLocation(profileDto.getLocation());
        restaurant.setOpeningSchedule(profileDto.getOpeningSchedule());
        restaurant.setClosingSchedule(profileDto.getClosingSchedule());

        ResponseEntity<ResponseInterfaceDto> saveOwner = this.saveOwner(owner);
        if (saveOwner != null)
            return saveOwner;
        
        ResponseEntity<ResponseInterfaceDto> saveRestaurant = this.saveRestaurant(restaurant);
        if (saveRestaurant != null)
            return saveRestaurant;

        return ResponseEntity.ok(profileDto);
    }

    public ResponseEntity<ResponseInterfaceDto> addMeal(
        Principal user,
        AddMealDto mealDto
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        Restaurant restaurant = daoUser.getRestaurant();

        RestaurantParser parser = new RestaurantParser();

        Meal meal = parser.parseAddMealDtoToMeal(mealDto);
        meal.setRestaurant(restaurant);
        
        try {
            meal = this.mealRepository.save(meal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ExceptionResponseDto(e.toString()));
        }

        restaurant.addMeal(meal);
        ResponseEntity<ResponseInterfaceDto> saveRestaurant = this.saveRestaurant(restaurant);
        if (saveRestaurant != null)
            return saveRestaurant;

        return ResponseEntity.ok(
            parser.parseMealtoMealDto(meal)
        );
    }

    public ResponseEntity<ResponseInterfaceDto> getMeal(
        Principal user,
        Long mealId
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        Meal meal = this.mealRepository.findById(mealId).orElseThrow(
            () -> new ResourceNotFoundException("Meal not found with id: " + mealId)
        );

        if (!meal.getRestaurant().equals(daoUser.getRestaurant()))
            throw new ResourceNotOwnedException("Meal is not owned by user with email: " + daoUser.getEmail());

        return ResponseEntity.ok(
            new RestaurantParser().parseMealtoMealDto(meal)
        );
    }

    private DAOUser retrieveRestaurantOwner(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(
            () -> new UsernameNotFoundException("User not found with email: " + email)
        );
    }

    private Restaurant retrieveRestaurant(DAOUser owner) {
        return this.restaurantRepository.findByOwner(
            owner
        ).orElseThrow(
            () -> new UsernameNotFoundException("Restaurant not found with owner email:" + owner.getEmail())
        );
    }

    private ResponseEntity<ResponseInterfaceDto> saveOwner(DAOUser owner) {
        try {
            this.userRepository.save(owner);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ExceptionResponseDto(e.toString()));
        }

        return null;
    }

    private ResponseEntity<ResponseInterfaceDto> saveRestaurant(Restaurant restaurant) {
        try {
            this.restaurantRepository.save(restaurant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ExceptionResponseDto(e.toString()));
        }

        return null;
    }

}
