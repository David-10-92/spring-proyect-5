package proyect5.realEstate.service;

import proyect5.realEstate.service.dtos.AveragePriceByProvinceDTO;
import proyect5.realEstate.service.dtos.InputDTO;

import java.util.List;

public interface AveragePriceByProvinceService {
    List<AveragePriceByProvinceDTO> generateReportNative(InputDTO inputDTO);
    List<AveragePriceByProvinceDTO> generateReportCriteria(InputDTO inputDTO);
}
