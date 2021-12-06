package pt.feup.les.feupfood.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "menus")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Schedule schedule;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Meal meatMeal;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Meal fishMeal;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Meal dietMeal;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    private Meal vegetarianMeal;

    @ManyToOne(fetch = FetchType.LAZY)
    private AssignMenu assignMenu;

}
