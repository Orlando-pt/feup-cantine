package pt.feup.les.feupfood.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pt.feup.les.feupfood.dto.AddAssignmentDto;
import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.AddMenuDto;
import pt.feup.les.feupfood.dto.ExceptionResponseDto;
import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.exceptions.ResourceNotOwnedException;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.repository.AssignMenuRepository;
import pt.feup.les.feupfood.repository.MealRepository;
import pt.feup.les.feupfood.repository.MenuRepository;
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

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private AssignMenuRepository assignMenuRepository;

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

    public ResponseEntity<ResponseInterfaceDto> addMenu(
        Principal user,
        AddMenuDto menuDto
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        Menu menu = new Menu();
        menu.setName(menuDto.getName());
        menu.setAdditionalInformation(menuDto.getAdditionalInformaiton());
        menu.setStartPrice(menuDto.getStartPrice());
        menu.setEndPrice(menuDto.getEndPrice());
        menu.setRestaurant(daoUser.getRestaurant());

        // add meals referent to the menu
        Meal meal = this.retrieveMeal(daoUser, menuDto.getMeatMealId());
        menu.addMeal(meal);
        meal.addMenu(menu);
        this.mealRepository.save(meal);
        this.menuRepository.save(menu);

        meal = this.retrieveMeal(daoUser, menuDto.getFishMealId());
        menu.addMeal(meal);
        meal.addMenu(menu);
        this.mealRepository.save(meal);
        this.menuRepository.save(menu);

        meal = this.retrieveMeal(daoUser, menuDto.getDietMealId());
        menu.addMeal(meal);
        meal.addMenu(menu);
        this.mealRepository.save(meal);
        this.menuRepository.save(menu);
        
        meal = this.retrieveMeal(daoUser, menuDto.getVegetarianMealId());
        menu.addMeal(meal);
        meal.addMenu(menu);
        this.mealRepository.save(meal);
        
        return ResponseEntity.ok(
            new RestaurantParser().parseMenutoMenuDto(
                this.menuRepository.save(menu)
            )
        );
    }

    public ResponseEntity<ResponseInterfaceDto> getMenu(
        Principal user,
        Long menuId
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());
        return ResponseEntity.ok(
            new RestaurantParser().parseMenutoMenuDto(
                this.retrieveMenu(daoUser, menuId)
            )
        );
    }

    public ResponseEntity<ResponseInterfaceDto> addAssignment(
        Principal user,
        AddAssignmentDto assignmentDto
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        AssignMenu assignment = new AssignMenu();
        assignment.setDate(assignmentDto.getDate());
        assignment.setSchedule(assignmentDto.getSchedule());
        assignment.setRestaurant(owner.getRestaurant());

        assignment.setMenu(
            this.retrieveMenu(owner, assignmentDto.getMenuId())
        );

        return ResponseEntity.ok(
            new RestaurantParser().parseAssignmentToAssignmentDto(
                this.assignMenuRepository.save(assignment)
            )
        );
    }

    public ResponseEntity<List<ResponseInterfaceDto>> getAssignments(
        Principal user
    ) {
        RestaurantParser parser = new RestaurantParser();
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        return ResponseEntity.ok(
            this.assignMenuRepository.findByRestaurant(owner.getRestaurant()).stream().map(
                (assignment) -> parser.parseAssignmentToAssignmentDto(assignment)
            ).collect(Collectors.toList())
        );
    }

    private Menu retrieveMenu(DAOUser user, Long menuId) {
        Menu menu = this.menuRepository.findById(menuId).orElseThrow(
            () -> new ResourceNotFoundException("No menu was found with id: " + menuId)
        );

        if (!menu.getRestaurant().equals(user.getRestaurant()))
            throw new ResourceNotOwnedException("Menu is not owned by user with email: " + user.getEmail());

        return menu;
    }

    private Meal retrieveMeal(DAOUser user, Long mealId) {
        Meal meal = this.mealRepository.findById(mealId).orElseThrow(
            () -> new ResourceNotFoundException("Meal not found with id: " + mealId)
        );

        if (!meal.getRestaurant().equals(user.getRestaurant()))
            throw new ResourceNotOwnedException("Meal is not owned by user with email: " + user.getEmail());

        return meal;
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
