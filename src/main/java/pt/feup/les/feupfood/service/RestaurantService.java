package pt.feup.les.feupfood.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pt.feup.les.feupfood.dto.RestaurantProfileDto;
import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.Restaurant;
import pt.feup.les.feupfood.repository.RestaurantRepository;
import pt.feup.les.feupfood.repository.UserRepository;

@Service
public class RestaurantService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

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

    public ResponseEntity<?> updateRestaurantProfile(
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

        ResponseEntity<Exception> saveOwner = this.saveOwner(owner);
        if (saveOwner != null)
            return saveOwner;
        
        ResponseEntity<Exception> saveRestaurant = this.saveRestaurant(restaurant);
        if (saveRestaurant != null)
            return saveRestaurant;

        return ResponseEntity.ok(profileDto);
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

    private ResponseEntity<Exception> saveOwner(DAOUser owner) {
        try {
            this.userRepository.save(owner);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }

        return null;
    }

    private ResponseEntity<Exception> saveRestaurant(Restaurant restaurant) {
        try {
            this.restaurantRepository.save(restaurant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }

        return null;
    }

}
