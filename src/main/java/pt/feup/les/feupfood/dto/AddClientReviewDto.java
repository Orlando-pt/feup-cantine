package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddClientReviewDto implements ResponseInterfaceDto {
    int classificationGrade;
    String comment;
}
