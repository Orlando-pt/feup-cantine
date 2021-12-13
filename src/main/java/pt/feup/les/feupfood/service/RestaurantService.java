package pt.feup.les.feupfood.service;

<<<<<<< HEAD
=======
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

>>>>>>> main
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
<<<<<<< HEAD
import pt.feup.les.feupfood.dto.*;
=======

import pt.feup.les.feupfood.dto.AddAssignmentDto;
import pt.feup.les.feupfood.dto.AddMealDto;
import pt.feup.les.feupfood.dto.AddMenuDto;
import pt.feup.les.feupfood.dto.ExceptionResponseDto;
import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
>>>>>>> main
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

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private AssignMenuRepository assignMenuRepository;

    // profile methods
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

    // meal methods
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

    // menu methods
    public ResponseEntity<GetPutMenuDto> addMenu(
        Principal user,
        AddMenuDto menuDto
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        Menu menu = new Menu();
        menu.setName(menuDto.getName());
        menu.setAdditionalInformation(menuDto.getAdditionalInformation());
        menu.setStartPrice(menuDto.getStartPrice());
        menu.setEndPrice(menuDto.getEndPrice());
        menu.setRestaurant(daoUser.getRestaurant());

        // add meals referent to the menu
        // TODO verify if that are of the correct mealtype
        Meal meal = null;
        if (menuDto.getMeatMeal() != null) {
            meal = this.retrieveMeal(daoUser, menuDto.getMeatMeal());
            menu.addMeal(meal);
            meal.addMenu(menu);
            this.mealRepository.save(meal);
        }

        if (menuDto.getFishMeal() != null) {
            meal = this.retrieveMeal(daoUser, menuDto.getFishMeal());
            menu.addMeal(meal);
            meal.addMenu(menu);
            this.mealRepository.save(meal);
        }

        if (menuDto.getDietMeal() != null) {
            meal = this.retrieveMeal(daoUser, menuDto.getDietMeal());
            menu.addMeal(meal);
            meal.addMenu(menu);
            this.mealRepository.save(meal);
        }
        
        if (menuDto.getVegetarianMeal() != null) {
            meal = this.retrieveMeal(daoUser, menuDto.getVegetarianMeal());
            menu.addMeal(meal);
            meal.addMenu(menu);
            this.mealRepository.save(meal);
        }
        
        if (menuDto.getDesertMeal() != null) {
            meal = this.retrieveMeal(daoUser, menuDto.getDesertMeal());
            menu.addMeal(meal);
            meal.addMenu(menu);
            this.mealRepository.save(meal);
        }

        return ResponseEntity.ok(
            new RestaurantParser().parseMenutoMenuDto(
                this.menuRepository.save(menu)
            )
        );
    }

    public ResponseEntity<List<GetPutMenuDto>> getMenus(
        Principal user
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());
        RestaurantParser parser = new RestaurantParser();

        return ResponseEntity.ok(
            daoUser.getRestaurant().getMenus().stream().map(
                parser::parseMenutoMenuDto
            ).collect(Collectors.toList())
        );
    }

    public ResponseEntity<GetPutMenuDto> getMenu(
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

    public ResponseEntity<GetPutMenuDto> updateMenu(
        Principal user,
        AddMenuDto menuDto,
        Long menuId
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        Menu menu = this.retrieveMenu(daoUser, menuId);

        // check if all meals remain the same
        List<Long> mealIds = Arrays.asList(
            menuDto.getDietMeal(),
            menuDto.getDesertMeal(),
            menuDto.getMeatMeal(),
            menuDto.getFishMeal(),
            menuDto.getVegetarianMeal()
        );

        mealIds = mealIds.stream().filter(
            Objects::nonNull
        ).collect(Collectors.toList());

        List<Long> currentMealIds = menu.getMeals().stream().map(
            Meal::getId
        ).collect(Collectors.toList());

        if (!currentMealIds.containsAll(mealIds)) {
            // if the lists are different
            // that means we need to update the list

            List<Meal> meals = menu.getMeals();
            Meal meal = null;
            // delete meals
            for (Long id : currentMealIds) {
                if (!mealIds.contains(id)) {
                    // update dto does not contain this id
                    meal = meals.get(currentMealIds.indexOf(id));
                    meal.removeMenu(menu);

                    menu.removeMeal(meal);
                    this.mealRepository.save(meal);
                }
            }

            // add meals
            for (Long id : mealIds) {
                if (!currentMealIds.contains(id)) {
                    // if the returned menu does not contained the meal, than we need to add it
                    meal = this.retrieveMeal(daoUser, id);
                    meal.addMenu(menu);

                    menu.addMeal(meal);
                    this.mealRepository.save(meal);
                }
            }
        }

        menu.setAdditionalInformation(menuDto.getAdditionalInformation());
        menu.setEndPrice(menuDto.getEndPrice());
        menu.setName(menuDto.getName());
        menu.setStartPrice(menuDto.getStartPrice());
        
        
        return ResponseEntity.ok(
            new RestaurantParser().parseMenutoMenuDto(
                this.menuRepository.save(menu)
            )
        );
    }

    public ResponseEntity<String> deleteMenu(
        Principal user,
        Long menuId
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        Menu menu = this.retrieveMenu(daoUser, menuId);
		menu.getMeals().forEach(
			meal -> {
				meal.removeMenu(menu);
				this.mealRepository.save(meal);
			}
		);
        this.menuRepository.delete(menu);
        
        return ResponseEntity.ok("");
    }

    // assignment methods
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
            this.retrieveMenu(owner, assignmentDto.getMenu())
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
            owner.getRestaurant().getAssignments().stream().map(
                assignment -> parser.parseAssignmentToAssignmentDto(assignment)
            ).collect(Collectors.toList())
        );
    }

    public ResponseEntity<GetAssignmentDto> updateAssignment(
        Principal user,
        AddAssignmentDto assignmentDto,
        Long assignmentId
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        AssignMenu assignment = this.retrieveAssignment(daoUser, assignmentId);

        assignment.setDate(assignmentDto.getDate());
        assignment.setSchedule(assignmentDto.getSchedule());

        if (assignmentDto.getMenu() != null && !assignmentDto.getMenu().equals(assignment.getMenu().getId())) {
            Menu menu = assignment.getMenu();
            menu.removeAssignment(assignment);
            this.menuRepository.save(menu);

            menu = this.retrieveMenu(daoUser, assignmentDto.getMenu());
            assignment.setMenu(menu);
            menu.addAssignment(assignment);
            this.menuRepository.save(menu);
        }

        return ResponseEntity.ok(
            new RestaurantParser().parseAssignmentToAssignmentDto(
                this.assignMenuRepository.save(assignment)
            )
        );
    }

    public ResponseEntity<String> deleteAssignment(
        Principal user,
        Long assignmentId
    ) {
        DAOUser daoUser = this.retrieveRestaurantOwner(user.getName());

        AssignMenu assignment = this.retrieveAssignment(daoUser, assignmentId);

        assignment.getMenu().removeAssignment(assignment);
        this.menuRepository.save(assignment.getMenu());

        assignment.getRestaurant().removeAssignment(assignment);
        this.restaurantRepository.save(assignment.getRestaurant());

        this.assignMenuRepository.delete(assignment);

        return ResponseEntity.ok("");
    }

    // auxiliar methods
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
        
        if (!owner.getRestaurant().equals(meal.getRestaurant()))
            throw new ResourceNotOwnedException("No meal matches the id [" + mealId + "] for user: " + owner.getEmail());

        return meal;
    }

    private Menu retrieveMenu(DAOUser user, Long menuId) {
        Menu menu = this.menuRepository.findById(menuId).orElseThrow(
            () -> new ResourceNotFoundException("No menu was found with id: " + menuId)
        );

        if (!menu.getRestaurant().equals(user.getRestaurant()))
            throw new ResourceNotOwnedException("Menu is not owned by user with email: " + user.getEmail());

        return menu;
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

    private AssignMenu retrieveAssignment(DAOUser owner, Long assignmentId) {
        AssignMenu assignment = this.assignMenuRepository.findById(
            assignmentId
        ).orElseThrow(
            () -> new ResourceNotFoundException("No assignment found with id: " + assignmentId)
        );

        if (!assignment.getRestaurant().equals(owner.getRestaurant()))
            throw new ResourceNotOwnedException("Assignment not owned.");

        return assignment;
    }
}
