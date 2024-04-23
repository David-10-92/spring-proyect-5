package proyect5.realEstate.service;

import proyect5.realEstate.service.dtos.InputDTO;
import proyect5.realEstate.service.dtos.PriceVariationFromAverageDTO;

import java.util.List;

public interface PriceVariationFromAverageService {
    List<PriceVariationFromAverageDTO> generateReportNative(InputDTO inputDTO);
    List<PriceVariationFromAverageDTO> generateReportCriteria(InputDTO inputDTO);
}
