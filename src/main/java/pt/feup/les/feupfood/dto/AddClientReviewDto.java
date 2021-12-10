package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddClientReviewDto implements ResponseInterfaceDto {
    Long restaurantId;
    int classificationGrade;
    String comment;
}
