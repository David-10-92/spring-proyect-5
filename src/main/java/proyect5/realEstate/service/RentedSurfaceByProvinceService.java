package proyect5.realEstate.service;

import proyect5.realEstate.persistence.dtos.InputDTO;
import proyect5.realEstate.persistence.dtos.RentedSurfaceByProvinceDTO;

import java.util.List;

public interface RentedSurfaceByProvinceService {
    List<RentedSurfaceByProvinceDTO> generateReport(InputDTO inputDTO);
}
