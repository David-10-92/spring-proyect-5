package proyect5.realEstate.service;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import proyect5.realEstate.persistence.dtos.InputDTO;
import proyect5.realEstate.persistence.dtos.RentReportDTO;

import java.util.Date;
import java.util.List;

public interface RentReportService {
    List<RentReportDTO> generateAllRentalReport(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date toDate,
            @RequestParam(value = "province", required = false) String provinceName
    );
}
