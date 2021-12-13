package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.feup.les.feupfood.model.Menu;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class AssignMenuDto implements ResponseInterfaceDto {
    private Date startingDate;
    private Date endDate;
    private List<Menu> menus;
}
