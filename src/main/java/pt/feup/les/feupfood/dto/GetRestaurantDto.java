package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class GetRestaurantDto implements ResponseInterfaceDto{
    private Long id;
    private String location;
    private Date openingSchedule;
    private Date closingSchedule;
}
