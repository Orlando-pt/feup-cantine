package pt.feup.les.feupfood.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponseDto implements ResponseInterfaceDto{
    
    private String exceptionContent;
}
