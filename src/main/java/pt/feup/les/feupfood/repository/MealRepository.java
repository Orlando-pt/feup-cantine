package pt.feup.les.feupfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealType;
import pt.feup.les.feupfood.model.Restaurant;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findMealsByMealType(MealType mealType);

    List<Meal> findByDescription(String description);

    List<Meal> findByNutritionalInformation(String nutritionalInformation);

    List<Meal> findByRestaurant(Restaurant restaurant);


}
