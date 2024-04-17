package proyect5.realEstate.persistence.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RentReportDTO {
    private Integer flatId;
    private String street;
    private String client;
    private Date from;
    private Date to;
    private String province;
    private String locality;
}
