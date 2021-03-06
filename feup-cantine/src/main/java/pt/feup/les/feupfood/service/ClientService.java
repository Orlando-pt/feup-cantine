package pt.feup.les.feupfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pt.feup.les.feupfood.dto.AddClientReviewDto;
import pt.feup.les.feupfood.dto.AddEatIntention;
import pt.feup.les.feupfood.dto.ClientStats;
import pt.feup.les.feupfood.dto.GetAssignmentDto;
import pt.feup.les.feupfood.dto.GetClientEatIntention;
import pt.feup.les.feupfood.dto.GetClientReviewDto;
import pt.feup.les.feupfood.dto.GetRestaurantDto;
import pt.feup.les.feupfood.dto.IsFavoriteDto;
import pt.feup.les.feupfood.dto.PriceRangeDto;
import pt.feup.les.feupfood.dto.PutClientEatIntention;
import pt.feup.les.feupfood.dto.ResponseInterfaceDto;
import pt.feup.les.feupfood.dto.UpdateProfileDto;
import pt.feup.les.feupfood.exceptions.BadRequestParametersException;
import pt.feup.les.feupfood.exceptions.DataIntegrityException;
import pt.feup.les.feupfood.exceptions.ExceededDateForAssignmentException;
import pt.feup.les.feupfood.exceptions.ResourceNotFoundException;
import pt.feup.les.feupfood.exceptions.ResourceNotOwnedException;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;
import pt.feup.les.feupfood.model.ScheduleEnum;
import pt.feup.les.feupfood.repository.AssignMenuRepository;
import pt.feup.les.feupfood.repository.EatIntentionRepository;
import pt.feup.les.feupfood.repository.MenuRepository;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.ReviewRepository;
import pt.feup.les.feupfood.repository.UserRepository;
import pt.feup.les.feupfood.util.ClientParser;

import java.security.Principal;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private AssignMenuRepository assignMenuRepository;

    @Autowired
    private EatIntentionRepository eatIntentionRepository;

    @Autowired
    private RestaurantService restaurantService;

    private static final String RESOURCE_NOT_OWNED_INTENTION_EXCEPTION = "Intention does not belong to the authenticated user.";

    // profile operations
    public ResponseEntity<UpdateProfileDto> getProfile(
        Principal user
    ) {
        return ResponseEntity.ok(
            new ClientParser().parseUserProfile(
                this.retrieveUser(user.getName())
            )
        );
    }

    public ResponseEntity<UpdateProfileDto> updateProfile(
        Principal user,
        UpdateProfileDto profileDto
    ) {
        DAOUser daoUser = this.retrieveUser(user.getName());

        daoUser.setFullName(profileDto.getFullName());
        daoUser.setBiography(profileDto.getBiography());
        daoUser.setProfileImageUrl(profileDto.getProfileImageUrl());

        daoUser = this.userRepository.save(daoUser);
        
        return ResponseEntity.ok(
            new ClientParser().parseUserProfile(daoUser)
        );
    }

    // review operations
    public ResponseEntity<ResponseInterfaceDto> saveReviewsFromRestaurantByRestaurantId(Long id, AddClientReviewDto clientReviewDto, Principal user) {
        DAOUser reviewer = this.retrieveUser(user.getName());
        Restaurant reviewedRestaurant = this.retrieveRestaurant(id);
        Review review = new Review();

        review.setClient(reviewer);
        review.setClassificationGrade(clientReviewDto.getClassificationGrade());
        review.setComment(clientReviewDto.getComment());
        review.setRestaurant(reviewedRestaurant);
        review.setTimestamp(new Timestamp(System.currentTimeMillis()));

        review = this.reviewRepository.save(review);

        // add review to the user
        reviewer.addReview(review);
        // add review to the restaurant
        reviewedRestaurant.addReview(review);

        this.userRepository.save(reviewer);
        this.restaurantRepository.save(reviewedRestaurant);

        return ResponseEntity.ok(new ClientParser().parseReviewToReviewDto(review));
    }

    public ResponseEntity<List<GetClientReviewDto>> getUserReviewsFromClient(Principal user) {

        DAOUser reviewer = this.retrieveUser(user.getName());

        return ResponseEntity.ok(this.getAllReviewsFromClient(reviewer));
    }

    // restaurant operations
    public ResponseEntity<PriceRangeDto> getPriceRangeOfRestaurant(Long id) {
        PriceRangeDto priceRange = new PriceRangeDto();

        Restaurant restaurant = this.retrieveRestaurant(id);

        if (restaurant.getMeals().size() != 0) {
            priceRange.setMinimumPrice(
                this.menuRepository.findFirstByRestaurant(
                    restaurant, Sort.by(Direction.ASC, "startPrice")
                ).getStartPrice()
            );

            priceRange.setMaximumPrice(
                this.menuRepository.findFirstByRestaurant(
                    restaurant, Sort.by(Direction.DESC, "endPrice")
                ).getEndPrice()
            );
        } else {
            priceRange.setMaximumPrice(0.0);
            priceRange.setMinimumPrice(0.0);
        }


        return ResponseEntity.ok(priceRange);
    }

    public ResponseEntity<List<GetRestaurantDto>> getAllRestaurants() {
        ClientParser clientParser = new ClientParser();
        return ResponseEntity.ok(this.restaurantRepository.findAll().stream().map(clientParser::parseRestaurantToRestaurantDto).collect(Collectors.toList()));
    }

    public ResponseEntity<List<GetClientReviewDto>> getReviewsFromRestaurantByRestaurantId(Long id) {
        Restaurant reviewedRestaurant = this.retrieveRestaurant(id);

        return ResponseEntity.ok(this.getAllReviewsFromRestaurant(reviewedRestaurant));
    }

    public ResponseEntity<ResponseInterfaceDto> getRestaurantById(Long restaurantId) {

        Restaurant restaurant = this.restaurantRepository.findById(restaurantId).orElseThrow(() -> new ResourceNotFoundException("The restaurant id was not found"));

        return ResponseEntity.ok(new ClientParser().parseRestaurantToRestaurantDto(restaurant));
    }

    // restaurant operations (get assignments)
    public ResponseEntity<List<GetAssignmentDto>> getAssignmentsOfRestaurant(
        Principal user,
        Long restaurantId
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        Restaurant restaurant = this.retrieveRestaurant(restaurantId);

        ClientParser parser = new ClientParser();
        
        return ResponseEntity.ok(
            restaurant.getAssignments().stream()
                .map(
                    assignment -> parser.parseAssignmentToAssignmentDto(assignment, client)
                )
                .collect(Collectors.toList())
        );
    }

    public ResponseEntity<List<GetAssignmentDto>> getAssignmentsOfRestaurantForNDays(
        Principal user,
        Long restaurantId,
        int days
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        Restaurant restaurant = this.retrieveRestaurant(restaurantId);

        Date now = new Date(System.currentTimeMillis());

        Date future = new Date(now.getTime() + days * 1000 * 60 * 60 * 24);

        ClientParser parser = new ClientParser();
        return ResponseEntity.ok(
            this.assignMenuRepository.findAllByDateBetweenAndRestaurant(
                now, future, restaurant
            ).stream().map(
                assignment -> parser.parseAssignmentToAssignmentDto(assignment, client)
            )
                .collect(Collectors.toList())
        );
    }

    public ResponseEntity<GetAssignmentDto> getCurrentAssignmentOfRestaurant(
        Principal user,
        Long restaurantId
    ) {
        DAOUser client = this.retrieveUser(user.getName());
        Restaurant restaurant = this.retrieveRestaurant(restaurantId);

        return ResponseEntity.ok(

            new ClientParser().parseAssignmentToAssignmentDto(
                this.restaurantService.getCurrentAssignment(restaurant),
                client
            )
        );
    }

    // add favorite restaurant operations
    public ResponseEntity<String> addFavoriteRestaurant(
        Principal user,
        Long restaurantId
    ) {
        DAOUser client = this.retrieveUser(user.getName());
        
        List<Long> favoriteRestaurantIds = client.getClientFavoriteRestaurants().stream()
                    .map(restaurant -> restaurant.getId())
                    .collect(Collectors.toList());

        if (favoriteRestaurantIds.contains(restaurantId))
            return ResponseEntity.badRequest().body("Restaurant with id [" + restaurantId + "] is already on favorites list.");

        Restaurant restaurant = this.retrieveRestaurant(restaurantId);

        client.addFavoriteRestaurant(restaurant);
        this.userRepository.save(client);

        return ResponseEntity.ok("Operation made successfuly");
    }

    public ResponseEntity<String> removeFavoriteRestaurant(
        Principal user,
        Long restaurantId
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        List<Long> favoriteRestaurantIds = client.getClientFavoriteRestaurants().stream()
                    .map(restaurant -> restaurant.getId())
                    .collect(Collectors.toList());

        if (!favoriteRestaurantIds.contains(restaurantId))
            return ResponseEntity.badRequest().body("Restaurant with id [" + restaurantId + "] not on favorites list");

        Restaurant restaurant = this.retrieveRestaurant(restaurantId);

        client.removeFavoriteRestaurant(restaurant);
        this.userRepository.save(client);

        return ResponseEntity.ok("Operation made successfuly");
    }

    public ResponseEntity<List<GetRestaurantDto>> getFavoriteRestaurants(
        Principal user
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        ClientParser parser = new ClientParser();
        return ResponseEntity.ok(
            client.getClientFavoriteRestaurants().stream()
                .map(parser::parseRestaurantToRestaurantDto)
                .collect(Collectors.toList())
        );
    }

    public ResponseEntity<IsFavoriteDto> restaurantIsFavorite(
        Principal user,
        Long restaurantId
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        return ResponseEntity.ok( new IsFavoriteDto(
                !client.getClientFavoriteRestaurants().stream().filter(
                    restaurant -> restaurant.getId().equals(restaurantId)
                )
                    .collect(Collectors.toList()).isEmpty()
            )
        );
    }

    // operations for client to provide eat intentions
    public ResponseEntity<GetClientEatIntention> addEatIntention(
        Principal user,
        AddEatIntention intentionDto
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        AssignMenu assignment = this.retrieveAssingment(intentionDto.getAssignmentId());
        
        // check if the assignment was already made
        List<EatIntention> intentionWasAlreadyMade = this.eatIntentionRepository.findByClientAndAssignment(client, assignment);

        if (!intentionWasAlreadyMade.isEmpty())
            throw new DataIntegrityException("One intention was already provided for assignment with id: " + assignment.getId());

        // verify if the operation is beeing made one day before the assignment
        this.verifyAddOrUpdateIntentionDate(assignment.getDate());

        Set<Long> assignmentMealsId = assignment.getMenu().getMeals().stream()
                    .map(meal -> meal.getId()).collect(Collectors.toSet());
                    
        // check if all meal ids belongs to this assignment
        if (!assignmentMealsId.containsAll(intentionDto.getMealsId()))
            throw new BadRequestParametersException("Not all meals belong to the assignment. Meals id: " + intentionDto.getMealsId().toString());

        Set<Meal> meals = assignment.getMenu().getMeals().stream()
                    .filter(meal -> intentionDto.getMealsId().contains(meal.getId()))
                    .collect(Collectors.toSet());

        EatIntention eatIntention = new EatIntention();
        eatIntention.setClient(client);
        eatIntention.setAssignment(assignment);
        eatIntention.setMeals(meals);

        eatIntention.setCode(this.generateRandomCode(assignment));
        eatIntention.setValidatedCode(false);

        return ResponseEntity.ok(
            new ClientParser().parseEatIntentionToDto(
                this.eatIntentionRepository.save(eatIntention))
            );
    }

    public ResponseEntity<String> removeEatIntention(
        Principal user,
        Long intentionId
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        EatIntention intention = this.retrieveIntention(intentionId);

        if (!intention.getClient().equals(client))
            throw new ResourceNotOwnedException(RESOURCE_NOT_OWNED_INTENTION_EXCEPTION);

        this.eatIntentionRepository.delete(intention);

        return ResponseEntity.ok("Intention deleted successfuly");
    }

    public ResponseEntity<GetClientEatIntention> updateEatIntention(
        Principal user,
        Long intentionId,
        PutClientEatIntention intentionDto
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        EatIntention intention = this.retrieveIntention(intentionId);

        if (!intention.getClient().equals(client))
            throw new ResourceNotOwnedException(RESOURCE_NOT_OWNED_INTENTION_EXCEPTION);

        // verify if is making the update one day before the assignment
        this.verifyAddOrUpdateIntentionDate(intention.getAssignment().getDate());

        Set<Long> currentMealsId = intention.getMeals().stream()
                        .map(Meal::getId)
                        .collect(Collectors.toSet());

        if (!currentMealsId.containsAll(intentionDto.getMealsId()) || currentMealsId.size() != intentionDto.getMealsId().size()) {
            Set<Meal> meals = intention.getAssignment().getMenu().getMeals()
                        .stream().filter(
                            meal -> intentionDto.getMealsId().contains(meal.getId())
                        ).collect(Collectors.toSet());
            
            intention.setMeals(meals);
            intention = this.eatIntentionRepository.save(intention);
        }

        return ResponseEntity.ok(
            new ClientParser().parseEatIntentionToDto(
                intention
            )
        );
    }

    public ResponseEntity<List<GetClientEatIntention>> getEatIntentions(
        Principal user
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        ClientParser parser = new ClientParser();
        return ResponseEntity.ok(
            client.getEatingIntentions().stream()
                .map(parser::parseEatIntentionToDto)
                .collect(Collectors.toList())
        );
    }

    public ResponseEntity<List<GetClientEatIntention>> getEatIntentionFromToday(
        Principal user
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        long currentTime = System.currentTimeMillis();
        Date yesterday = new Date(currentTime - (currentTime % (1000L * 60 * 60 * 24)) - 10000);
        Date today = new Date(currentTime);
        Calendar now = Calendar.getInstance();

        ClientParser parser = new ClientParser();

        return ResponseEntity.ok(
            client.getEatingIntentions().stream()
                .filter(
                    intention -> intention.getAssignment().getDate().after(yesterday)
                )
                .filter(
                    intention -> intention.getAssignment().getDate().before(today) &&
                                    now.get(Calendar.HOUR_OF_DAY) < 17 &&
                                    intention.getAssignment().getSchedule() == ScheduleEnum.LUNCH ||
                                    intention.getAssignment().getDate().before(today) &&
                                    now.get(Calendar.HOUR_OF_DAY) > 16 &&
                                    intention.getAssignment().getSchedule() == ScheduleEnum.DINNER ||
                                    intention.getAssignment().getDate().after(today)
                )
                .map(
                    parser::parseEatIntentionToDto
                ).collect(
                    Collectors.toList()
                )
        );

    }

    public ResponseEntity<GetClientEatIntention> getEatIntention(
        Principal user,
        Long intentionId
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        EatIntention intention = this.retrieveIntention(intentionId);

        if (!intention.getClient().equals(client))
            throw new ResourceNotOwnedException(RESOURCE_NOT_OWNED_INTENTION_EXCEPTION);
        
        return ResponseEntity.ok(
            new ClientParser().parseEatIntentionToDto(intention)
        );
    }

    public ResponseEntity<GetClientEatIntention> getNextEatIntention(
        Principal user
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        // filter through only the next intentions
        long currentTime = System.currentTimeMillis();
        Date yesterday = new Date(currentTime - (currentTime % (1000L * 60 * 60 * 24)) - 10000);
        Calendar now = Calendar.getInstance();

        List<EatIntention> intentions = client.getEatingIntentions().stream()
                        .filter(
                            intention -> intention.getAssignment().getDate().after(yesterday) &&
                                            !intention.getValidatedCode()
                        )
                        .sorted(
                            (intention1, intention2) -> intention1.getAssignment().getDate()
                                                            .compareTo(intention2.getAssignment().getDate())
                        ).collect(Collectors.toList());

        if (intentions.isEmpty())
            return ResponseEntity.ok(new GetClientEatIntention());
        
        // check if the first 2 dates are the same
        // if they are then we need to filter the lunch meal
        ClientParser parser = new ClientParser();

        if (intentions.size() != 1 &&
            intentions.get(0).getAssignment().getDate()
                .equals(intentions.get(1).getAssignment().getDate()))
                return now.get(Calendar.HOUR_OF_DAY) > 16 ?
                    ResponseEntity.ok(parser.parseEatIntentionToDto(
                        this.getEatingIntentionOfDay(intentions, ScheduleEnum.DINNER)
                    )) :
                    ResponseEntity.ok(parser.parseEatIntentionToDto(
                        this.getEatingIntentionOfDay(intentions, ScheduleEnum.LUNCH)
                    ));

        return ResponseEntity.ok(
            parser.parseEatIntentionToDto(intentions.get(0))
        );
    }

    private EatIntention getEatingIntentionOfDay(List<EatIntention> intentions, ScheduleEnum schedule) {
        return intentions.get(0).getAssignment().getSchedule() == schedule ?
                    intentions.get(0) :
                    intentions.get(1);
    }

    // stats methods
    public ResponseEntity<ClientStats> getMoneySaved(
        Principal user
    ) {
        DAOUser client = this.retrieveUser(user.getName());

        ClientStats stats = new ClientStats();

        Date now = new Date(System.currentTimeMillis());

        List<EatIntention> intentionsGiven = this.eatIntentionRepository.findByClient(client);

        intentionsGiven.forEach(
            intention -> {
                stats.incrementIntentionsGiven();
                if (intention.getValidatedCode()) {
                    stats.addMoney(intention.getAssignment().getMenu().getDiscount().doubleValue());
                } else {
                    // increment on the intentions not fulfilled if the date of the intention is before today
                    if (intention.getAssignment().getDate().before(now))
                        stats.incrementIntentionsNotFulfilled();
                }
            }
        );

        stats.setNumberOfReviews(client.getReviews().size());

        stats.setNumberOfFavoritRestaurants(client.getClientFavoriteRestaurants().size());

        return ResponseEntity.ok(stats);
    }

    // auxiliar methods
    private DAOUser retrieveUser(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private Restaurant retrieveRestaurant(Long restaurantId) {
        return this.restaurantRepository.findById(restaurantId).orElseThrow(() -> new UsernameNotFoundException("Restaurant not found with id:" + restaurantId));
    }

    private AssignMenu retrieveAssingment(Long assignmentId) {
        return this.assignMenuRepository.findById(assignmentId).orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));
    }

    private EatIntention retrieveIntention(Long intentionId) {
        return this.eatIntentionRepository.findById(intentionId).orElseThrow(() -> new ResourceNotFoundException("Eating intention not found with id: " + intentionId));
    }
    
    private List<GetClientReviewDto> getAllReviewsFromClient(DAOUser client) {
        ClientParser clientParser = new ClientParser();
        return client.getReviews().stream().map(clientParser::parseReviewToReviewDto).collect(Collectors.toList());
    }

    private List<GetClientReviewDto> getAllReviewsFromRestaurant(Restaurant restaurant) {
        ClientParser clientParser = new ClientParser();
        return restaurant.getReviews().stream().map(clientParser::parseReviewToReviewDto).collect(Collectors.toList());
    }

    private String generateRandomCode(AssignMenu assignment) {
        String generatedCode = "";
        SecureRandom random = new SecureRandom();
        boolean validCode = false;

        List<String> listWithAssignmentCodes = assignment.getEatingIntentions().stream()
            .map(EatIntention::getCode).collect(Collectors.toList());

        while (!validCode) {
            generatedCode = "";
            
            for (int i = 0; i < 9; i++)
                generatedCode += String.valueOf(random.nextInt(10));
            
            // check if code generated was already assigned to anyone
            // only in this assignment
            // generate a new code if one already exists
            validCode = !listWithAssignmentCodes.contains(generatedCode);
        }

        return generatedCode;
    }

    private void verifyAddOrUpdateIntentionDate(Date assignmentDate) {
        long oneDay = 1000L * 60 * 60 * 24;
        Date tomorrow = new Date(System.currentTimeMillis() + oneDay);

        if (tomorrow.after(assignmentDate))
            throw new ExceededDateForAssignmentException("Eating intentions need to be made 1 day before of the assignment.");

    }
}
