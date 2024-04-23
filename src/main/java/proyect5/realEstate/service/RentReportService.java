package proyect5.realEstate.service;

import proyect5.realEstate.service.dtos.InputDTO;
import proyect5.realEstate.service.dtos.RentReportDTO;

import java.util.List;

public interface RentReportService {
    List<RentReportDTO> generateAllRentalReportNative(InputDTO inputDTO);
    List<RentReportDTO> generateAllRentalReportCriteria(InputDTO inputDTO);
}
