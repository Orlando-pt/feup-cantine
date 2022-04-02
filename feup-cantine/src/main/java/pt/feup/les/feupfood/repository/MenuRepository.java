package pt.feup.les.feupfood.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.feup.les.feupfood.model.Menu;
import pt.feup.les.feupfood.model.Restaurant;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    List<Menu> findByName(String name);
    Menu findFirstByRestaurant(Restaurant restaurant, Sort sort);
}
