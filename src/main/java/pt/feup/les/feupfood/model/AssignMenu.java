package pt.feup.les.feupfood.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"restaurant", "menu", "eatingIntentions"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = AssignMenu.class)
@Entity
@Table(name = "assignments")
public class AssignMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ScheduleEnum schedule;

    @ManyToOne(optional = false)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ToString.Exclude
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<EatIntention> eatingIntentions;

    public AssignMenu() {
        this.eatingIntentions = new ArrayList<>();
    }

}
