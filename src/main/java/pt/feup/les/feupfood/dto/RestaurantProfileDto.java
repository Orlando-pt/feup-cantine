package pt.feup.les.feupfood.dto;

import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestaurantProfileDto implements ResponseInterfaceDto{

    private String fullName;
    private String location;
    private Date openingSchedule;
    private Date closingSchedule;
    
}
