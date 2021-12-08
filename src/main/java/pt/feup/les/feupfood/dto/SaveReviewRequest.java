package pt.feup.les.feupfood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class SaveReviewRequest {

    @NotBlank(message = "User id cannot be null")
    Long userId;
    @NotBlank(message = "Restaurant id cannot be null")
    Long restaurantId;
    @NotBlank(message = "Classification Grade cannot be null")
    String classificationGrade;
    @NotBlank(message = "Comment cannot be null")
    String comment;


}
