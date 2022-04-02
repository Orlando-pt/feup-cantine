package pt.feup.les.feupfood.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.feup.les.feupfood.model.ScheduleEnum;

@Data
@NoArgsConstructor
public class AddAssignmentDto implements ResponseInterfaceDto{

    private Date date;
    private ScheduleEnum schedule;
    private Long menu;
    
}
