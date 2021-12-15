package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetPutClientReviewDto implements ResponseInterfaceDto {
    Long id;
    Long clientId;
    String clientFullName;
    Long restaurantId;
    int classificationGrade;
    String comment;
}
