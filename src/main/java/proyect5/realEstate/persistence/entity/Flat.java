package proyect5.realEstate.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.Order;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "flats")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Flat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "locality_id")
    private Locality locality;
    private String address;
    private double area;
    private boolean furnished;
    @Temporal(TemporalType.DATE)
    private Date reformed;
    private double price;
    @OneToMany(mappedBy = "flat")
    private List<Rent> rents;
}
