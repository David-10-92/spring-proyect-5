package proyect5.realEstate.persistence.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AveragePriceByProvinceDTO {
    private String province;
    private double averagePrice;
}
