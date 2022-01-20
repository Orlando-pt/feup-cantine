package pt.feup.les.feupfood.service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import pt.feup.les.feupfood.dto.GetClientReviewDto;
import pt.feup.les.feupfood.dto.GetPutMealDto;
import pt.feup.les.feupfood.dto.GetPutMenuDto;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.RestaurantAnswerReviewDto;
import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.dto.StatsIntentionDto;
import pt.feup.les.feupfood.dto.VerifyCodeDto;
import pt.feup.les.feupfood.exceptions.BadRequestParametersException;
import pt.feup.les.feupfood.exceptions.DataIntegrityException;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.exceptions.ResourceNotOwnedException;
import pt.feup.les.feupfood.exceptions.VerificationCodeException;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.Meal;
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
import pt.feup.les.feupfood.util.ClientParser;
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

    @Autowired
    private EatIntentionRepository eatIntentionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private Clock clock;


    private final static long MS_PER_DAY = 1000L * 60 * 60 * 24;

    // profile methods
    public ResponseEntity<RestaurantProfileDto> getRestaurantProfile(
            Principal user
    ) {
        RestaurantProfileDto restaurantDto = new RestaurantProfileDto();

        DAOUser owner = this.retrieveRestaurantOwner(user.getName());
        Restaurant restaurant = this.retrieveRestaurant(owner);

        restaurantDto.setFullName(owner.getFullName());
        restaurantDto.setProfileImageUrl(owner.getProfileImageUrl());
        restaurantDto.setLocation(restaurant.getLocation());
        restaurantDto.setCuisines(restaurant.getCuisines());
        restaurantDto.setTypeMeals(restaurant.getTypeMeals());
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
        owner.setProfileImageUrl(profileDto.getProfileImageUrl());

        restaurant.setLocation(profileDto.getLocation());
        restaurant.setCuisines(profileDto.getCuisines());
        restaurant.setTypeMeals(profileDto.getTypeMeals());
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
        menu.setDiscount(menuDto.getDiscount());
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
        menu.setDiscount(menuDto.getDiscount());
        
        
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

        // verify if the assignment was alreay made for this date
        List<AssignMenu> dayAssignments = this.assignMenuRepository.findByDateAndRestaurant(assignmentDto.getDate(), owner.getRestaurant());

        dayAssignments.forEach(
            assignment -> {
                if (assignment.getSchedule() == assignmentDto.getSchedule())
                    throw new DataIntegrityException("There is already one assignment for the current schedule.");
            }
        );

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

    public ResponseEntity<VerifyCodeDto> verifyCode(
        Principal user,
        Long assignmentId,
        String code
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        AssignMenu assignment = this.retrieveAssignment(owner, assignmentId);

        List<EatIntention> intentionList = assignment.getEatingIntentions().stream()
            .filter(eatIntention -> eatIntention.getCode().equals(code))
            .collect(Collectors.toList());
        if (intentionList.isEmpty())
            throw new VerificationCodeException("The specified code was not found.");

        // it is lacking the verification if it was given more than two intentions
        // because we really trust there will no appear duplicated codes

        EatIntention intention = intentionList.get(0);

        if (intention.getValidatedCode())
            throw new VerificationCodeException("This code was already validated");

        intention.setValidatedCode(true);
        this.eatIntentionRepository.save(intention);

        return ResponseEntity.ok(new RestaurantParser().parseUserToVerifyCodeDto(
            intention.getClient(), intention.getMeals()
        ));
    }

    public ResponseEntity<VerifyCodeDto> verifyCodeAutomatically(
        Principal user,
        String code
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        AssignMenu currentAssignment = this.getCurrentAssignment(owner.getRestaurant());

        return this.verifyCode(
            user,
            currentAssignment.getId(),
            code
        );
    }

    public ResponseEntity<GetAssignmentDto> getCurrentAssignment(
        Principal user
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        return ResponseEntity.ok(
            new RestaurantParser().parseAssignmentToAssignmentDto(
                this.getCurrentAssignment(owner.getRestaurant())
            )
        );
    }

    public ResponseEntity<List<GetAssignmentDto>> getAssignmentsNextNDays(
        Principal user,
        int days
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        Date now = new Date(System.currentTimeMillis());

        Date future = new Date(now.getTime() + days * 1000 * 60 * 60 * 24);

        RestaurantParser parser = new RestaurantParser();
        return ResponseEntity.ok(
            this.assignMenuRepository.findAllByDateBetweenAndRestaurant(
                now, future, owner.getRestaurant()
            ).stream().map(parser::parseAssignmentToAssignmentDto)
                .collect(Collectors.toList())
        );
    }

    // review methods
    public ResponseEntity<List<GetClientReviewDto>> getRestaurantReviews(
        Principal user
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        ClientParser parser = new ClientParser();
        return ResponseEntity.ok(
            this.reviewRepository.findAllByRestaurant(owner.getRestaurant())
                .stream().map(
                    parser::parseReviewToReviewDto
                ).collect(Collectors.toList())
        );
    }

    public ResponseEntity<GetClientReviewDto> getRestaurantReview(
        Principal user,
        Long reviewId
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        return ResponseEntity.ok(
            new ClientParser().parseReviewToReviewDto(this.retrieveReview(owner.getRestaurant(), reviewId))
        );
    }

    public ResponseEntity<GetClientReviewDto> restaurantAnswerToReview(
        Principal user,
        Long reviewId,
        RestaurantAnswerReviewDto dto
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        Review review = this.retrieveReview(owner.getRestaurant(), reviewId);

        review.setAnswer(dto.getAnswer());
        review = this.reviewRepository.save(review);

        return ResponseEntity.ok(
            new ClientParser().parseReviewToReviewDto(review)
        );
    }

    // stat methods
    public ResponseEntity<Map<Date, StatsIntentionDto>> getNumberOfIntentionsInAPeriod(
        Principal user,
        int increment,
        Date start,
        Date end
    ) {
        if (start.after(end))
            throw new BadRequestParametersException("End date is before start date");

        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        Map<Date, StatsIntentionDto> mapOfNumberOfIntentions = new LinkedHashMap<>();
        List<AssignMenu> assignments;
        StatsIntentionDto stats;

        start.setTime(start.getTime() - (start.getTime() % MS_PER_DAY));
        end.setTime(end.getTime() - (end.getTime() % MS_PER_DAY) + 60000L);

        long incrementToDate = increment * MS_PER_DAY;

        Date currentEndDate = new Date(start.getTime() + incrementToDate - 60000L);

        // while the current date is before the end date
        // keep inserting stats in the map
        while(start.before(end)) {
            if (currentEndDate.after(end))
                currentEndDate.setTime(end.getTime());
                
            assignments = this.assignMenuRepository.findAllByDateBetweenAndRestaurant(start, currentEndDate, owner.getRestaurant());

            stats = new StatsIntentionDto();

            for (AssignMenu assignment : assignments) {
                stats.addIntentionsGiven(assignment.getEatingIntentions().size());

                for (EatIntention intention : assignment.getEatingIntentions())
                    if (intention.getValidatedCode())
                        stats.incrementIntentionsFulfilled();
                    else
                        stats.incrementIntentionsNotFulfilled();
            }

            mapOfNumberOfIntentions.put(
                new Date(start.getTime()),
                stats
            );

            // increment current date
            start.setTime(start.getTime() + incrementToDate);
            currentEndDate.setTime(currentEndDate.getTime() + incrementToDate);
        }


        return ResponseEntity.ok(
            mapOfNumberOfIntentions
        );
    }

    public ResponseEntity<Map<Date, Float>> getPopularityOfRestaurant(
        Principal user,
        int increment,
        Date start,
        Date end
    ) {
        if (start.after(end))
            throw new BadRequestParametersException("End date is before start date");
            
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        Map<Date, Float> popularityMap = new LinkedHashMap<>();

        long incrementToDate = increment * MS_PER_DAY;

        Timestamp startTimestamp = new Timestamp(start.getTime());
        Timestamp endTimestamp = new Timestamp(end.getTime());
        Timestamp currentEndTimestamp = new Timestamp(start.getTime());

        List<Review> reviews;

        // while the current date is different from the end date
        // keep inserting stats in the map
        while(!currentEndTimestamp.equals(endTimestamp)) {
            // increment current date
            currentEndTimestamp.setTime(currentEndTimestamp.getTime() + incrementToDate);

            // if the current end date is after the end date
            // then put the current end date equal to the stipulated end date
            if (currentEndTimestamp.after(end))
                currentEndTimestamp.setTime(end.getTime());
                
            reviews = this.reviewRepository.findAllByTimestampBetweenAndRestaurant(startTimestamp, currentEndTimestamp, owner.getRestaurant());

            popularityMap.put(
                new Date(currentEndTimestamp.getTime() - incrementToDate),
                reviews.stream().map(
                    review -> review.getClassificationGrade()
                ).reduce(
                    0,
                    (subtotal, classification) -> subtotal + classification
                ).floatValue() / reviews.size()
            );
        }

        return ResponseEntity.ok(popularityMap);
    }

    public ResponseEntity<Map<GetPutMealDto, Integer>> getMostFrequentMeals(
        Principal user,
        int numberOfMeals
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        Map<GetPutMealDto, Integer> favoriteMealsMap = new LinkedHashMap<>();
        List<Meal> meals = this.mealRepository.findByRestaurant(owner.getRestaurant());

        RestaurantParser parser = new RestaurantParser();

        meals = meals.stream().sorted(
                (meal1, meal2) -> meal2.getEatingIntentions().size() - meal1.getEatingIntentions().size()
        ).collect(Collectors.toList());

        if (numberOfMeals < meals.size())
            meals = meals.subList(0, numberOfMeals);

        meals.forEach(
            meal -> favoriteMealsMap.put(
                parser.parseMealtoMealDto(meal),
                meal.getEatingIntentions().size()
            )
        );
        return ResponseEntity.ok(favoriteMealsMap);
    }

    public ResponseEntity<Map<String, Number>> getGeneralStats(
        Principal user
    ) {
        DAOUser owner = this.retrieveRestaurantOwner(user.getName());

        Map<String, Number> generalStatsMap = new LinkedHashMap<>();

        generalStatsMap.put("totalReviewsReceived", owner.getRestaurant().getReviews().size());
        generalStatsMap.put(
            "totalReviewsResponded",
            (int) owner.getRestaurant().getReviews().stream()
                .filter(
                    review -> review.getAnswer() != null && !review.getAnswer().isEmpty()
                ).count()
        );
        generalStatsMap.put(
            "totalIntentsReceived",
            owner.getRestaurant().getAssignments().stream()
                .map(
                    assignment -> assignment.getEatingIntentions().size()
                ).reduce(
                    0, (subtotal, intentions) -> subtotal + intentions
                )
        );
        generalStatsMap.put(
            "moneyOffered",
            owner.getRestaurant().getAssignments().stream()
                .map(assignment -> assignment.getMenu().getDiscount() * assignment.getEatingIntentions().size())
                .reduce(
                    0.0, (subTotal, moneySaved) -> subTotal + moneySaved
                )
        );
        generalStatsMap.put(
            "assignmentsCreated",
            owner.getRestaurant().getAssignments().size()
        );
        generalStatsMap.put(
            "menusCreated",
            owner.getRestaurant().getMenus().size()
        );
        generalStatsMap.put(
            "mealsCreated",
            owner.getRestaurant().getMeals().size()
        );
        generalStatsMap.put(
            "favorited",
            owner.getRestaurant().getFavoritedClients().size()
        );

        return ResponseEntity.ok(generalStatsMap);
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

    private Review retrieveReview(Restaurant restaurant, Long reviewId) {
        Review review = this.reviewRepository.findById(reviewId).orElseThrow(
            () -> new ResourceNotFoundException("No review found with id: " + reviewId)
        );

        if (!review.getRestaurant().equals(restaurant))
            throw new ResourceNotOwnedException("Review not owned.");

        return review;
    }

    protected AssignMenu getCurrentAssignment(Restaurant restaurant) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.clock.millis());

        Date now = calendar.getTime();

        List<AssignMenu> assignments = this.assignMenuRepository.findByDateAndRestaurant(now, restaurant);

        if (assignments.isEmpty())
            throw new ResourceNotFoundException("No assignments were found for today.");

        if (assignments.size() == 1)
            return assignments.get(0);

        if (assignments.size() != 2)
            throw new DataIntegrityException("There are more than two assignments for today.");

        // else it means there are two assignments for this day
        // we need to see which one is more appropriate for the time being
        
        // dinner will be after 5pm
        if (calendar.get(Calendar.HOUR_OF_DAY) > 16)
            return assignments.stream().filter(
                    assignment -> assignment.getSchedule() == ScheduleEnum.DINNER
                ).collect(Collectors.toList()).get(0);

        // if the code gets here it means its before 5pm
        // in that can we can suppose that the meal is lunch
        return assignments.stream().filter(
                assignment -> assignment.getSchedule() == ScheduleEnum.LUNCH
            ).collect(Collectors.toList()).get(0);
    }
}
