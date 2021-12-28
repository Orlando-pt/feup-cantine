package pt.feup.les.feupfood.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = {"reviews", "restaurant", "clientFavoriteRestaurants"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = DAOUser.class)
@Entity
@Table(name = "users")
public class DAOUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String profileImageUrl;

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

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "client_fav_restaurants",
        joinColumns = @JoinColumn(name = "restaurant_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id")
    )
    private List<Restaurant> clientFavoriteRestaurants;

    public DAOUser() {
        this.reviews = new ArrayList<>();
        this.clientFavoriteRestaurants = new ArrayList<>();
    }

    public boolean addReview(Review review) {
        return this.reviews.add(review);
    }

    public boolean addFavoriteRestaurant(Restaurant restaurant) {
        return this.clientFavoriteRestaurants.add(restaurant);
    }

    public boolean removeFavoriteRestaurant(Restaurant restaurant) {
        return this.clientFavoriteRestaurants.remove(restaurant);
    }
}
