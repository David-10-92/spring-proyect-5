package proyect5.realEstate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import proyect5.realEstate.persistence.dtos.AveragePriceByProvinceDTO;
import proyect5.realEstate.persistence.dtos.InputDTO;
import proyect5.realEstate.persistence.dtos.RentReportDTO;
import proyect5.realEstate.persistence.dtos.RentedSurfaceByProvinceDTO;
import proyect5.realEstate.service.AveragePriceByProvinceService;
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

    @Autowired
    private AveragePriceByProvinceService averagePriceByProvinceService;

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

    @GetMapping("/averagePriceByProvince")
    public ResponseEntity<List<AveragePriceByProvinceDTO>> generateAveragePriceByProvinceReport(
            InputDTO inputDTO) {

        List<AveragePriceByProvinceDTO> report = averagePriceByProvinceService.generateReport(inputDTO);
        return ResponseEntity.ok(report);
    }
}
