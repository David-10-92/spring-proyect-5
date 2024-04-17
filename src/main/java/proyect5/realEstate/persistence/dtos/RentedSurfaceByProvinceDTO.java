package proyect5.realEstate.persistence.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RentedSurfaceByProvinceDTO {
    private String province;
    private Long totalFlats;
    private double totalSurface;
}


