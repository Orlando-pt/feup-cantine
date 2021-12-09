package pt.feup.les.feupfood.model;

import java.sql.Time;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
}
