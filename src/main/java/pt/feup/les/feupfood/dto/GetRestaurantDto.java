package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@NoArgsConstructor
public class GetRestaurantDto implements ResponseInterfaceDto{
    private Long id;
    private String fullName;
    private String location;
    private String profileImageUrl;
    private Time morningOpeningSchedule;
    private Time morningClosingSchedule;
    private Time afternoonOpeningSchedule;
    private Time afternoonClosingSchedule;

}
