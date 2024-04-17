package proyect5.realEstate.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyect5.realEstate.persistence.dtos.AveragePriceByProvinceDTO;
import proyect5.realEstate.persistence.dtos.InputDTO;
import proyect5.realEstate.persistence.entity.Flat;
import proyect5.realEstate.persistence.entity.Locality;
import proyect5.realEstate.persistence.entity.Province;
import proyect5.realEstate.persistence.entity.Rent;
import proyect5.realEstate.service.AveragePriceByProvinceService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AveragePriceByProvinceServiceImpl implements AveragePriceByProvinceService {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<AveragePriceByProvinceDTO> generateReport(InputDTO inputDTO) {
        //Obtener los parámetros de entrada desde el objeto InputDTO
        Date fromDate = inputDTO.getFrom();
        Date toDate = inputDTO.getTo();
        String provinceName = inputDTO.getProvince();

        //Crear un CriteriaBuilder para construir la consulta
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        //Crear un CriteriaQuery con el tipo de resultado deseado
        CriteriaQuery<AveragePriceByProvinceDTO> cq = cb.createQuery(AveragePriceByProvinceDTO.class);

        //Crear un objeto Root para especificar la entidad principal sobre la que se realizará la consulta
        Root<Rent> rentRoot = cq.from(Rent.class);

        //Realizar las joins necesarias para acceder a las entidades relacionadas
        Join<Rent, Flat> flatJoin = rentRoot.join("flat");
        Join<Flat, Locality> localityJoin = flatJoin.join("locality");
        Join<Locality, Province> provinceJoin = localityJoin.join("province");

        //Especificar las expresiones de selección
        cq.multiselect(
                provinceJoin.get("name"), // Nombre de la provincia
                cb.avg(rentRoot.get("price")) // Precio medio del alquiler
        ).groupBy(provinceJoin.get("name")); // Agrupar por nombre de provincia

        //Aplicar filtro de fecha si es proporcionado
        List<Predicate> predicates = new ArrayList<>();
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(rentRoot.get("from"), fromDate, toDate));
        }

        //Agregar filtro por nombre de provincia si se proporciona
        if (provinceName != null && !provinceName.isEmpty()) {
            predicates.add(cb.equal(cb.lower(provinceJoin.get("name")), provinceName.toLowerCase()));
        }

        //Si no se proporciona el nombre de la provincia, no aplicar filtro y obtener todos los resultados
        if (predicates.isEmpty()) {
            cq.where();
        } else {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        //Ejecutar la consulta y obtener los resultados
        TypedQuery<AveragePriceByProvinceDTO> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
