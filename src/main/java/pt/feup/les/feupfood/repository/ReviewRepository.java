package pt.feup.les.feupfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.feup.les.feupfood.model.User;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findAllByRestaurant(Restaurant restaurant);
    List<Review> findAllByUser(User user);
}
