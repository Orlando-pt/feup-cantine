package pt.feup.les.feupfood.dto;

import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetClientReviewDto implements ResponseInterfaceDto {
    private Long id;
    private Long clientId;
    private String clientFullName;
    private String clientProfileImageUrl;
    private Long restaurantId;
    private int classificationGrade;
    private String comment;
    private String answer;
    private Timestamp timestamp;
}
