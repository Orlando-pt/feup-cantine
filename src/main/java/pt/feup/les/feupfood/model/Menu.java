package pt.feup.les.feupfood.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Data
@EqualsAndHashCode(exclude = {"meals", "assignments"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Menu.class)
@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String additionalInformation;

    private Double startPrice;

    private Double endPrice;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "menu_meals",
        joinColumns = @JoinColumn(name = "meal_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name ="menu_id", referencedColumnName = "id")
        )
    private List<Meal> meals;

    @ToString.Exclude
    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AssignMenu> assignments;

    public Menu() {
        this.meals = new ArrayList<>();
        this.assignments = new ArrayList<>();
    }

    public boolean addMeal(Meal meal) {
        return this.meals.add(meal);
    }

    public boolean addAssignment(AssignMenu assignment) {
        return this.assignments.add(assignment);
    }

}
