package proyect5.realEstate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import proyect5.realEstate.persistence.dtos.InputDTO;
import proyect5.realEstate.persistence.dtos.RentReportDTO;
import proyect5.realEstate.persistence.dtos.RentedSurfaceByProvinceDTO;
import proyect5.realEstate.service.RentReportService;
import proyect5.realEstate.service.RentedSurfaceByProvinceService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class RentReportController {
    @Autowired
    private RentReportService rentReportService;
    @Autowired
    private RentedSurfaceByProvinceService rentedSurfaceByProvinceService;

    @GetMapping("/allRents")
    public ResponseEntity<List<RentReportDTO>> generateAllRentReport(InputDTO inputDTO){

        List<RentReportDTO> rentReport = rentReportService.generateAllRentalReport(inputDTO);
        return ResponseEntity.ok(rentReport);
    }

    @GetMapping("/rentedSurfaceByProvince")
    public ResponseEntity<List<RentedSurfaceByProvinceDTO>> generateRentedSurfaceByProvinceReport(
            InputDTO inputDTO) {

        List<RentedSurfaceByProvinceDTO> report = rentedSurfaceByProvinceService.generateReport(inputDTO);
        return ResponseEntity.ok(report);
    }
}
