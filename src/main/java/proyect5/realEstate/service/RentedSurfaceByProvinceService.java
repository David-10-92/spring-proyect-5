package proyect5.realEstate.service;

import proyect5.realEstate.service.dtos.InputDTO;
import proyect5.realEstate.service.dtos.RentedSurfaceByProvinceDTO;

import java.util.List;

public interface RentedSurfaceByProvinceService {
    List<RentedSurfaceByProvinceDTO> generateReportNative(InputDTO inputDTO);
    List<RentedSurfaceByProvinceDTO> generateReportCriteria(InputDTO inputDTO);
}
