package pt.feup.les.feupfood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.repository.RestaurantRepository;

@RestController
public class HomeController {

    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @GetMapping("/")
    public String index() {
        var restaurant = new Restaurant();
        restaurant.setName("Dona Alzira");
        restaurant.setLocation("On the corner");
        this.restaurantRepository.save(
            restaurant
        );
        return "Hello hello!";
    }
}
