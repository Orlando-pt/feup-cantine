package pt.feup.les.feupfood.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import pt.feup.les.feupfood.model.Restaurant;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {

    @Query("{name:'?0'}")
    Optional<Restaurant> findRestaurantByName(String name);
    
}
