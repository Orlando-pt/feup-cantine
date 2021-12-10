package pt.feup.les.feupfood.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = "reviews")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = DAOUser.class)
@Entity
@Table(name = "users")
public class DAOUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "varchar(20) check (role in ('ROLE_ADMIN', 'ROLE_USER_CLIENT', 'ROLE_USER_RESTAURANT'))")
    private String role;

    private Boolean terms;

    private String biography;

    @ToString.Exclude
    @OneToMany(mappedBy="client")
    private List <Review> reviews;

    // in the case user is a restaurant
    @ToString.Exclude
    @OneToOne(mappedBy = "owner")
    private Restaurant restaurant;

    public DAOUser() {
        this.reviews = new ArrayList<>();
    }

    public boolean addReview(Review review) {
        return this.reviews.add(review);
    }
}
