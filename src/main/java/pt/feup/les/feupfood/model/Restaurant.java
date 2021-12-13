package pt.feup.les.feupfood.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import java.sql.Time;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = {"meals", "assignments", "menus", "reviews"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Restaurant.class)
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private DAOUser owner;

    private String location;

    @Basic
    private Time morningOpeningSchedule;

    @Basic
    private Time morningClosingSchedule;

    @Basic
    private Time afternoonOpeningSchedule;

    @Basic
    private Time afternoonClosingSchedule;

    @ToString.Exclude
    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Meal> meals;

    @ToString.Exclude
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    List <Review> reviews;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AssignMenu> assignments;

    @ToString.Exclude
    @OneToMany(mappedBy = "restaurant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Menu> menus;

    public Restaurant() {
        this.meals = new ArrayList<>();
        this.assignments = new ArrayList<>();
        this.menus = new ArrayList<>();
    }

    public boolean addMeal(Meal meal) {
        return this.meals.add(meal);
    }

    public boolean addReview(Review review) {
        return this.reviews.add(review);
    }
    public boolean addAssignment(AssignMenu assignment) {
        return this.assignments.add(assignment);
    }

    public boolean removeAssignment(AssignMenu assignment) {
        return this.assignments.remove(assignment);
    }

    public boolean addMenu(Menu menu) {
        return this.menus.add(menu);
    }
    
}
