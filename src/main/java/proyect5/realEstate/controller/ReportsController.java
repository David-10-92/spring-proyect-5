package proyect5.realEstate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyect5.realEstate.service.AveragePriceByProvinceService;
import proyect5.realEstate.service.PriceVariationFromAverageService;
import proyect5.realEstate.service.RentReportService;
import proyect5.realEstate.service.RentedSurfaceByProvinceService;
import proyect5.realEstate.service.dtos.*;
import proyect5.realEstate.service.error.ServiceError;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    private <T> ResponseEntity handleRequest(Supplier<T> supplier){
        try{
            return ResponseEntity.ok(supplier.get());
        }catch(ServiceError e){
            return new ResponseEntity(e.getMessage(), HttpStatusCode.valueOf(e.getErrorCode().getHttpErrorCode()));
        }
    }

    @Autowired
    private RentReportService rentReportService;

    @Autowired
    private RentedSurfaceByProvinceService rentedSurfaceByProvinceService;

    @Autowired
    private AveragePriceByProvinceService averagePriceByProvinceService;

    @Autowired
    private PriceVariationFromAverageService priceVariationFromAverageService;

    @GetMapping("/allRents")
    public ResponseEntity<List<RentReportDTO>> generateAllRentReport(InputDTO inputDTO){
        return handleRequest( ()-> rentReportService.generateAllRentalReportCriteria(inputDTO));
    }

    @GetMapping("/rentedSurfaceByProvince")
    public ResponseEntity<List<RentedSurfaceByProvinceDTO>> generateRentedSurfaceByProvinceReport(
            InputDTO inputDTO) {
        return handleRequest( ()-> rentedSurfaceByProvinceService.generateReportCriteria(inputDTO));
    }

    @GetMapping("/averagePriceByProvince")
    public ResponseEntity<List<AveragePriceByProvinceDTO>> generateAveragePriceByProvinceReport(
            InputDTO inputDTO) {
        return handleRequest( ()-> averagePriceByProvinceService.generateReportCriteria(inputDTO));
    }

    @GetMapping("/priceVariationFromAverageByProvince")
    public ResponseEntity<List<PriceVariationFromAverageDTO>> generatePriceVariationReport(InputDTO inputDTO) {
        return handleRequest( ()-> priceVariationFromAverageService.generateReportCriteria(inputDTO));
    }
}
