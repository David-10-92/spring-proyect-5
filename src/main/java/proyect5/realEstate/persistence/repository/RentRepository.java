package proyect5.realEstate.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import proyect5.realEstate.persistence.entity.Rent;

public interface RentRepository extends CrudRepository<Rent,Integer> {
}
