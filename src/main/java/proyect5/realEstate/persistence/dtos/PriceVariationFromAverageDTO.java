package proyect5.realEstate.persistence.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceVariationFromAverageDTO {
    private Integer flatId;
    private String street;
    private String province;
    private String locality;
    private double flatPrice;
    private double averagePrice;
    private double variation;
}
