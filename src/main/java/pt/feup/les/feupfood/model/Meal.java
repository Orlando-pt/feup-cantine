package pt.feup.les.feupfood.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = {"restaurant", "menus", "eatingIntentions"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Meal.class)
@Entity
@Table(name = "meals")
public class Meal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private MealTypeEnum mealType;

    @Column(nullable = false)
    private String description;

    private String nutritionalInformation;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "meals")
    private List<Menu> menus;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "meals")
    private List<EatIntention> eatingIntentions;

    public Meal() {
        this.menus = new ArrayList<>();
        this.eatingIntentions = new ArrayList<>();
    }

    public boolean addMenu(Menu menu) {
        return this.menus.add(menu);
    }

    public boolean removeMenu(Menu menu) {
        return this.menus.remove(menu);
    }
}
