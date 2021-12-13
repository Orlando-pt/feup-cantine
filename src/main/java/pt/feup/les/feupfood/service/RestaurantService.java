package pt.feup.les.feupfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pt.feup.les.feupfood.dto.*;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.exceptions.ResourceNotOwnedException;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.repository.MealRepository;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.UserRepository;
import pt.feup.les.feupfood.util.RestaurantParser;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
        restaurantDto.setMorningOpeningSchedule(restaurant.getMorningOpeningSchedule());
        restaurantDto.setMorningClosingSchedule(restaurant.getMorningClosingSchedule());
        restaurantDto.setAfternoonOpeningSchedule(restaurant.getAfternoonOpeningSchedule());
        restaurantDto.setAfternoonClosingSchedule(restaurant.getAfternoonClosingSchedule());

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
        restaurant.setMorningOpeningSchedule(profileDto.getMorningOpeningSchedule());
        restaurant.setMorningClosingSchedule(profileDto.getMorningClosingSchedule());
        restaurant.setAfternoonOpeningSchedule(profileDto.getAfternoonOpeningSchedule());
        restaurant.setAfternoonClosingSchedule(profileDto.getAfternoonClosingSchedule());

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

    public ResponseEntity<List<GetPutMealDto>> getMeals(Principal user) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());
        RestaurantParser parser = new RestaurantParser();

        return ResponseEntity.ok(daoUser.getRestaurant().getMeals().stream().map(
                parser::parseMealtoMealDto
        ).collect(Collectors.toList()));
    }

    public ResponseEntity<GetPutMealDto> updateMeal(
            Principal user,
            Long mealId,
            AddMealDto mealDto
    ) {

        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        Meal meal = this.retrieveMeal(daoUser, mealId);

        meal.setMealType(mealDto.getMealType());
        meal.setDescription(mealDto.getDescription());
        meal.setNutritionalInformation(mealDto.getNutritionalInformation());

        meal = this.mealRepository.save(meal);

        return ResponseEntity.ok(
                new RestaurantParser().parseMealtoMealDto(meal)
        );
    }

    public ResponseEntity<String> deleteMeal(
            Principal user,
            Long mealId
    ) {

        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        Meal meal = this.retrieveMeal(daoUser, mealId);
        this.mealRepository.delete(meal);

        return ResponseEntity.ok("");
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

    private Meal retrieveMeal(DAOUser owner, Long mealId) {
        Meal meal = this.mealRepository.findById(mealId).orElseThrow(
                () -> new ResourceNotFoundException("No Meal matches the id: " + mealId)
        );

        System.out.println(owner.getRestaurant().equals(meal.getRestaurant()));

        if (!owner.getRestaurant().equals(meal.getRestaurant()))
            throw new ResourceNotOwnedException("No meal matches the id [" + mealId + "] for user: " + owner.getEmail());

        return meal;
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
