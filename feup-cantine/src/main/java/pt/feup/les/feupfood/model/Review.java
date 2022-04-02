package pt.feup.les.feupfood.model;

import lombok.*;

import java.sql.Timestamp;

import javax.persistence.*;

@Entity
@Table(name = "reviews")
@Data
@EqualsAndHashCode(exclude = {"restaurant", "client"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @SequenceGenerator(name = "review_generator", sequenceName = "review_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_generator")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_T_U"))
    DAOUser client;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false, foreignKey = @ForeignKey(name = "FK_T_R"))
    Restaurant restaurant;

    int classificationGrade;

    @Column(name = "comment")
    String comment;

    String answer;

    @Basic
    Timestamp timestamp;

    public void setClassificationGrade(int classificationGrade) {
        if (classificationGrade > 0 && classificationGrade <= 5)
            this.classificationGrade = classificationGrade;
        else throw new IllegalArgumentException("Invalid classification, the classification should be between 0 and 5");
    }

}
