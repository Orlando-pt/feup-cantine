package pt.feup.les.feupfood.dto;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.feup.les.feupfood.model.ScheduleEnum;

@Data
@NoArgsConstructor
public class GetAssignmentDto implements ResponseInterfaceDto{

    private Long id;
    private Date date;
    private ScheduleEnum schedule;
    private GetPutMenuDto menu;
    
}
