package pt.feup.les.feupfood.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @SequenceGenerator(name="review_generator", sequenceName = "review_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_generator")
    Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false, foreignKey = @ForeignKey(name="FK_T_U"))
    User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable=false, foreignKey = @ForeignKey(name="FK_T_R"))
    Restaurant restaurant;

    @Column(name = "classification_grade")
    String classificationGrade;

    @Column(name = "comment")
    String comment;
}
