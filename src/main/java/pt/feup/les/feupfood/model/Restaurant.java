package pt.feup.les.feupfood.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = "reviews")
@Entity
@Table(name = "restaurants")
public class Restaurant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private DAOUser owner;

    @ToString.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    List <Review> reviews;
    
    private String location;

    private Date openingSchedule;

    private Date closingSchedule;

    public Restaurant() {
        this.reviews = new ArrayList<>();
    }

    public boolean addReview(Review review) {
        return this.reviews.add(review);
    }
}
