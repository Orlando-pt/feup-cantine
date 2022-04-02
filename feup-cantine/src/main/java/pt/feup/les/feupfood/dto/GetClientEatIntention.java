package pt.feup.les.feupfood.dto;

import java.util.Set;

import lombok.Data;

@Data
public class GetClientEatIntention {

    private Long id;
    private GetAssignmentDto assignment;
    private Set<GetPutMealDto> meals;
    private String code;
    private Boolean validatedCode;
    private String restaurant;
}
