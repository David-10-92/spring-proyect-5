package proyect5.realEstate.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import proyect5.realEstate.persistence.entity.Client;

public interface ClientRepository extends CrudRepository<Client,Integer> {
}
