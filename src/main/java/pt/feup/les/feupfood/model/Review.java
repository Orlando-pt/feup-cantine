package pt.feup.les.feupfood.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "review")
@Data
@EqualsAndHashCode(exclude = {"restaurant", "client"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @SequenceGenerator(name="review_generator", sequenceName = "review_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_generator")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false, foreignKey = @ForeignKey(name="FK_T_U"))
    DAOUser client;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable=false, foreignKey = @ForeignKey(name="FK_T_R"))
    Restaurant restaurant;

    @Column(name = "classification_grade")
    int classificationGrade;

    @Column(name = "comment")
    String comment;
    
}
