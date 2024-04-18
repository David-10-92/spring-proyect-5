package proyect5.realEstate.service;

import proyect5.realEstate.persistence.dtos.InputDTO;
import proyect5.realEstate.persistence.dtos.PriceVariationFromAverageDTO;

import java.util.List;

public interface PriceVariationFromAverageService {
    List<PriceVariationFromAverageDTO> generateReport(InputDTO inputDTO);
}
