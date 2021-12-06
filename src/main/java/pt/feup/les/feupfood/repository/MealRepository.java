package pt.feup.les.feupfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.feup.les.feupfood.model.Meal;
import pt.feup.les.feupfood.model.MealTypeEnum;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findMealsByMealType(MealTypeEnum mealType);
    
}
