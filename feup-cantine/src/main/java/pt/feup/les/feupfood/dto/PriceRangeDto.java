package pt.feup.les.feupfood.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PriceRangeDto {

    private Double minimumPrice;
    private Double maximumPrice;
    
}
