package proyect5.realEstate.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyect5.realEstate.service.dtos.InputDTO;
import proyect5.realEstate.service.dtos.RentReportDTO;
import proyect5.realEstate.persistence.entity.Flat;
import proyect5.realEstate.persistence.entity.Locality;
import proyect5.realEstate.persistence.entity.Province;
import proyect5.realEstate.persistence.entity.Rent;
import proyect5.realEstate.service.RentReportService;
import proyect5.realEstate.service.error.ErrorCode;
import proyect5.realEstate.service.error.ServiceError;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;

@Service
public class RentReportServiceImpl implements RentReportService {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<RentReportDTO> generateAllRentalReportNative(InputDTO inputDTO) {
        // Obtener los parámetros de entrada desde el objeto InputDTO
        Date fromDate = inputDTO.getFrom();
        Date toDate = inputDTO.getTo();
        String provinceName = inputDTO.getProvince();

        // Validar que los parámetros de entrada no estén vacíos
        if (fromDate == null || toDate == null || provinceName == null || provinceName.isEmpty()) {
            throw new ServiceError(ErrorCode.INVALID_INPUT,"Los parámetros de entrada no pueden estar vacíos");
        }
        // Construir la consulta SQL nativa
        String sql = "SELECT "
                + "    f.id AS flatId, "
                + "    f.address AS street, "
                + "    CONCAT(c.name, ' ', c.surname) AS client, "
                + "    r.from AS fromDate, "
                + "    r.to AS toDate, "
                + "    p.name AS province, "
                + "    l.name AS locality "
                + "FROM "
                + "    rents r "
                + "    JOIN flats f ON r.flat_id = f.id "
                + "    JOIN clients c ON r.client_id = c.id "
                + "    JOIN localities l ON f.locality_id = l.id "
                + "    JOIN provinces p ON l.province_id = p.id "
                + "WHERE "
                + "    (:fromDate IS NULL OR :toDate IS NULL OR r.from BETWEEN :fromDate AND :toDate) "
                + "    AND (:provinceName IS NULL OR LOWER(p.name) LIKE :provinceName)";

        // Crear la consulta SQL nativa
        Query query = entityManager.createNativeQuery(sql);

        // Establecer parámetros si es necesario
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("provinceName", provinceName != null ? "%" + inputDTO.getProvince().toLowerCase() + "%" : null);

        // Obtener los resultados y mapearlos al DTO
        List<Object[]> resultList = query.getResultList();
        List<RentReportDTO> rentReportDTOList = new ArrayList<>();
        for (Object[] result : resultList) {
            RentReportDTO rentReportDTO = new RentReportDTO();
            rentReportDTO.setFlatId((Integer) result[0]);
            rentReportDTO.setStreet((String) result[1]);
            rentReportDTO.setClient((String) result[2]);
            rentReportDTO.setFrom((Date) result[3]);
            rentReportDTO.setTo((Date) result[4]);
            rentReportDTO.setProvince((String) result[5]);
            rentReportDTO.setLocality((String) result[6]);
            rentReportDTOList.add(rentReportDTO);
        }

        return rentReportDTOList;
    }

    @Override
    public List<RentReportDTO> generateAllRentalReportCriteria(InputDTO inputDTO) {

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
