package pt.feup.les.feupfood.dto;

import java.util.Set;

import lombok.Data;

@Data
public class PutClientEatIntention {
    
    private Set<Long> mealsId;
}
