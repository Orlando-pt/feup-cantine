package pt.feup.les.feupfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.feup.les.feupfood.model.AssignMenu;
import pt.feup.les.feupfood.model.Restaurant;

import java.util.Date;
import java.util.List;

@Repository
public interface AssignMenuRepository extends JpaRepository<AssignMenu, Long> {

    List<AssignMenu> findByRestaurant(Restaurant restaurant);
    List<AssignMenu> findAllByDateBetween(Date startDate, Date endDate);
}
