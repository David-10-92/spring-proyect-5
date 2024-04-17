package proyect5.realEstate.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import proyect5.realEstate.persistence.dtos.InputDTO;
import proyect5.realEstate.persistence.dtos.RentReportDTO;
import proyect5.realEstate.persistence.entity.Flat;
import proyect5.realEstate.persistence.entity.Locality;
import proyect5.realEstate.persistence.entity.Province;
import proyect5.realEstate.persistence.entity.Rent;
import proyect5.realEstate.service.RentReportService;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

@Service
public class RentReportServiceImpl implements RentReportService {

    @Autowired
    private EntityManager entityManager;
    @Override
    public List<RentReportDTO> generateAllRentalReport(InputDTO inputDTO) {

        //Crear un objeto CriteriaBuilder
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        //Crear un objeto CriteriaQuery con el tipo de resultado deseado
        CriteriaQuery<RentReportDTO> cq = cb.createQuery(RentReportDTO.class);
        //Crear un objeto Root para especificar la entidad principal sobre la que se
        //realizara la consulta
        Root<Rent> rentRoot = cq.from(Rent.class);
        //Crear una lista para almacenar los predicados que se agregaran a la consulta
        List<Predicate> predicates = new ArrayList<>();
        //Agregar un predicado para filtrar los contratos dentro del período dado
        predicates.add(cb.between(rentRoot.get("from"),inputDTO.getFrom(),inputDTO.getTo()));
        //Si se proporciona el nombre de la provincia, agregar un predicado
        //para filtrar por provincia
        if(inputDTO.getProvince() != null){
            Join<Rent, Flat> flatJoin = rentRoot.join("flat");
            Join<Flat, Locality> localityJoin = flatJoin.join("locality");
            Join<Locality, Province> provinceJoin = localityJoin.join("province");
            predicates.add(cb.like(cb.lower(provinceJoin.get("name")),"%" +
                    inputDTO.getProvince().toLowerCase() + "%"));
        }

        //Creando una expresion para contatenar el nombre completo
        Expression<String> fullName = cb.concat(
                cb.concat(
                        rentRoot.get("client").get("name"),
                        " "),
                    rentRoot.get("client").get("surname")

        );
        //Construir la consulta seleccionando los campos deseados y aplicando los predicados
        cq.select(cb.construct(RentReportDTO.class,
                rentRoot.get("flat").get("id"),
                rentRoot.get("flat").get("address"),
                fullName,
                rentRoot.get("from"),
                rentRoot.get("to"),
                rentRoot.get("flat").get("locality").get("province").get("name"),
                rentRoot.get("flat").get("locality").get("name")))
                .where(predicates.toArray((new Predicate[0])));
        //Ejecutar la consulta y devolver los resultados

        return entityManager.createQuery(cq).getResultList();
    }
}
