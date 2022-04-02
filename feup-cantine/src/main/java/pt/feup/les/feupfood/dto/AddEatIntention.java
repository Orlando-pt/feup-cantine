package pt.feup.les.feupfood.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class AddEatIntention {

    private Long assignmentId;
    private Set<Long> mealsId;

    public AddEatIntention() {
        this.mealsId = new HashSet<>();
    }
}
