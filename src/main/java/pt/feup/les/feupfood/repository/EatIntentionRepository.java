package pt.feup.les.feupfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.feup.les.feupfood.model.EatIntention;

@Repository
public interface EatIntentionRepository extends JpaRepository<EatIntention, Long>{
    
}
