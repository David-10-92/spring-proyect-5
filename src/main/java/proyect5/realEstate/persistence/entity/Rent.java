package proyect5.realEstate.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import proyect5.realEstate.persistence.dtos.PriceVariationFromAverageDTO;
import proyect5.realEstate.persistence.dtos.RentReportDTO;

import java.util.Date;
@Entity
@Table(name = "rents")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SqlResultSetMapping(
        name = "PriceVariationFromAverageMapping",
        classes = {
                @ConstructorResult(
                        targetClass = PriceVariationFromAverageDTO.class,
                        columns = {
                                @ColumnResult(name = "flatId", type = Integer.class),
                                @ColumnResult(name = "street", type = String.class),
                                @ColumnResult(name = "province", type = String.class),
                                @ColumnResult(name = "locality", type = String.class),
                                @ColumnResult(name = "flatPrice", type = Double.class),
                                @ColumnResult(name = "averagePrice", type = Double.class)
                        }
                )
        }
)
public class Rent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "flat_id")
    private Flat flat;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @Temporal(TemporalType.DATE)
    private Date from;
    @Temporal(TemporalType.DATE)
    private Date to;
    private double price;
}
