package pt.feup.les.feupfood.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.feup.les.feupfood.model.DAOUser;

@Repository
public interface UserRepository extends JpaRepository<DAOUser, Long>{
    
    Optional<DAOUser> findByUsername(String username);
}
