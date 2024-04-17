package proyect5.realEstate.service;

import proyect5.realEstate.persistence.dtos.AveragePriceByProvinceDTO;
import proyect5.realEstate.persistence.dtos.InputDTO;

import java.util.List;

public interface AveragePriceByProvinceService {
    List<AveragePriceByProvinceDTO> generateReport(InputDTO inputDTO);
}
