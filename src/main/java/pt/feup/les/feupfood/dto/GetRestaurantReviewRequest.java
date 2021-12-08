package pt.feup.les.feupfood.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;


@Data
public class GetRestaurantReviewRequest {
    @NotBlank(message = "Restaurant id cannot be  null.")
    Long restaurantId;

}
