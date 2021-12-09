package pt.feup.les.feupfood.dto;

import java.sql.Time;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestaurantProfileDto implements ResponseInterfaceDto{

    private String fullName;
    private String location;
    private Time morningOpeningSchedule;
    private Time morningClosingSchedule;
    private Time afternoonOpeningSchedule;
    private Time afternoonClosingSchedule;
}
