package pt.feup.les.feupfood.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;

@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private Restaurant restaurantAlzira;
    private Restaurant restaurantDeolinda;
    
    private DAOUser ownerAlzira;
    private DAOUser ownerDeolinda;
    private DAOUser client1;

    private Review reviewClient1;

    public ReviewRepositoryTest() {
        this.restaurantAlzira = new Restaurant();
        this.restaurantDeolinda = new Restaurant();

        this.ownerAlzira = new DAOUser();
        this.ownerDeolinda = new DAOUser();
        this.client1 = new DAOUser();

        this.reviewClient1 = new Review();

        this.ownerAlzira.setFullName("Alzira Esteves");
        this.ownerAlzira.setPassword("password");
        this.ownerAlzira.setEmail("email");
        this.ownerAlzira.setRole("ROLE_USER_RESTAURANT");
        this.ownerAlzira.setTerms(true);


        this.ownerDeolinda.setFullName("Deolinda Macarrão");
        this.ownerDeolinda.setPassword("password");
        this.ownerDeolinda.setEmail("emaildadeolinda");
        this.ownerDeolinda.setRole("ROLE_USER_RESTAURANT");
        this.ownerDeolinda.setTerms(true);

        this.client1.setFullName("John");
        this.client1.setPassword("password");
        this.client1.setEmail("emailsomething");
        this.client1.setRole("ROLE_USER_CLIENT");
        this.client1.setTerms(true);

        this.restaurantAlzira.setOwner(this.ownerAlzira);
        this.restaurantAlzira.setLocation("In the corner");

        this.restaurantDeolinda.setOwner(this.ownerDeolinda);
        this.restaurantDeolinda.setLocation("In the other corner");

        this.ownerAlzira.setRestaurant(restaurantAlzira);
        this.ownerDeolinda.setRestaurant(restaurantDeolinda);

        this.reviewClient1.setComment("The food was great");
        this.reviewClient1.setClassificationGrade(5);
        this.reviewClient1.setClient(this.client1);
        this.reviewClient1.setRestaurant(this.restaurantAlzira);

        this.restaurantAlzira.addReview(this.reviewClient1);
        this.client1.addReview(this.reviewClient1);
    }

    @BeforeEach
    void setUp() {
        this.entityManager.clear();

        this.entityManager.persist(this.ownerAlzira);
        this.entityManager.persist(this.ownerDeolinda);
        this.entityManager.persist(this.client1);

        this.entityManager.persist(this.restaurantAlzira);
        this.entityManager.persist(this.restaurantDeolinda);
        this.entityManager.persist(this.reviewClient1);

        this.entityManager.flush();
    }

    @Test
    void testFindByRestaurant () {
        Assertions.assertThat(
                this.reviewRepository.findAllByRestaurant(
                        this.restaurantAlzira
                )
        ).hasSize(1).contains(this.reviewClient1);
    }

    @Test
    void testFindByClient () {
        Assertions.assertThat(
                this.reviewRepository.findAllByClient(
                        this.client1
                )
        ).hasSize(1).contains(this.reviewClient1);
    }
}
