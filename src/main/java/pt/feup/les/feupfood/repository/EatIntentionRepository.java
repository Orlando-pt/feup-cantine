package pt.feup.les.feupfood.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.feup.les.feupfood.model.DAOUser;
import pt.feup.les.feupfood.model.EatIntention;

@Repository
public interface EatIntentionRepository extends JpaRepository<EatIntention, Long>{
    
}
