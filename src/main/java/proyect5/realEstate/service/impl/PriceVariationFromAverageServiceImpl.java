package proyect5.realEstate.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyect5.realEstate.persistence.dtos.InputDTO;
import proyect5.realEstate.persistence.dtos.PriceVariationFromAverageDTO;
import proyect5.realEstate.persistence.entity.Flat;
import proyect5.realEstate.persistence.entity.Locality;
import proyect5.realEstate.persistence.entity.Province;
import proyect5.realEstate.persistence.entity.Rent;
import proyect5.realEstate.service.PriceVariationFromAverageService;
import proyect5.realEstate.service.error.ErrorCode;
import proyect5.realEstate.service.error.ServiceError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PriceVariationFromAverageServiceImpl implements PriceVariationFromAverageService {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<PriceVariationFromAverageDTO> generateReport(InputDTO inputDTO) {
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
                + "    p.name AS province, "
                + "    l.name AS locality, "
                + "    r.price AS flatPrice, "
                + "    (SELECT AVG(f2.price) FROM flats f2 JOIN localities l2 ON f2.locality_id = l2.id JOIN provinces p2 ON l2.province_id = p2.id WHERE p2.name = :provinceName) AS averagePrice, "
                + "    ((r.price - (SELECT AVG(f2.price) FROM flats f2 JOIN localities l2 ON f2.locality_id = l2.id JOIN provinces p2 ON l2.province_id = p2.id WHERE p2.name = :provinceName)) / (SELECT AVG(f2.price) FROM flats f2 JOIN localities l2 ON f2.locality_id = l2.id JOIN provinces p2 ON l2.province_id = p2.id WHERE p2.name = :provinceName)) * 100 AS variation "
                + "FROM "
                + "    rents r "
                + "    JOIN flats f ON r.flat_id = f.id "
                + "    JOIN localities l ON f.locality_id = l.id "
                + "    JOIN provinces p ON l.province_id = p.id "
                + "WHERE "
                + "    (:fromDate IS NULL OR :toDate IS NULL OR r.from BETWEEN :fromDate AND :toDate) "
                + "    AND (:provinceName IS NULL OR LOWER(p.name) = :provinceName) "
                + "GROUP BY "
                + "    f.id, f.address, p.name, l.name, r.price";

        // Crear la consulta SQL nativa
        Query query = entityManager.createNativeQuery(sql, "PriceVariationFromAverageMapping");

        // Establecer parámetros si es necesario
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("provinceName", provinceName != null ? provinceName.toLowerCase() : null);

        // Ejecutar la consulta y devolver los resultados
        return query.getResultList();
    }
    /*@Override
    public List<PriceVariationFromAverageDTO> generateReport(InputDTO inputDTO) {
        // Obtener los parámetros de entrada desde el objeto InputDTO
        Date fromDate = inputDTO.getFrom();
        Date toDate = inputDTO.getTo();
        String provinceName = inputDTO.getProvince();

        // Construir la Criteria Query
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PriceVariationFromAverageDTO> criteriaQuery = criteriaBuilder.createQuery(PriceVariationFromAverageDTO.class);
        Root<Rent> rentRoot = criteriaQuery.from(Rent.class);
        Join<Rent, Flat> flatJoin = rentRoot.join("flat");
        Join<Flat, Locality> localityJoin = flatJoin.join("locality");
        Join<Locality, Province> provinceJoin = localityJoin.join("province");

        // Subconsulta para calcular la suma total de los precios de los pisos por provincia
        Subquery<Double> sumSubquery = criteriaQuery.subquery(Double.class);
        Root<Rent> sumRentRoot = sumSubquery.from(Rent.class);
        Join<Rent, Flat> sumFlatJoin = sumRentRoot.join("flat");
        Join<Flat, Locality> sumLocalityJoin = sumFlatJoin.join("locality");
        Join<Locality, Province> sumProvinceJoin = sumLocalityJoin.join("province");

        sumSubquery.select(criteriaBuilder.sum(sumFlatJoin.get("price")))
                .where(criteriaBuilder.equal(sumProvinceJoin.get("name"), provinceJoin.get("name")));

        // Subconsulta para contar la cantidad de pisos por provincia
        Subquery<Long> countSubquery = criteriaQuery.subquery(Long.class);
        Root<Flat> countFlatRoot = countSubquery.from(Flat.class);
        Join<Flat, Locality> countLocalityJoin = countFlatRoot.join("locality");
        Join<Locality, Province> countProvinceJoin = countLocalityJoin.join("province");

        countSubquery.select(criteriaBuilder.count(countFlatRoot))
                .where(criteriaBuilder.equal(countProvinceJoin.get("name"), provinceJoin.get("name")));

        // Obtener la media del precio de los pisos por provincia
        Expression<Double> sumPrice = criteriaBuilder.sum(sumSubquery.getSelection());
        Expression<Long> countFlats = criteriaBuilder.count(countSubquery.getSelection());
        Expression<Double> averagePrice = criteriaBuilder.quot(sumPrice, criteriaBuilder.prod(countFlats, 1.0)).as(Double.class);

        // Definir las selecciones
        criteriaQuery.multiselect(
                flatJoin.get("id").alias("flatId"),
                flatJoin.get("address").alias("street"),
                provinceJoin.get("name").alias("province"),
                localityJoin.get("name").alias("locality"),
                rentRoot.get("price").alias("flatPrice"),
                averagePrice.alias("averagePrice"), // Utilizar la media calculada aquí
                criteriaBuilder.prod(
                        criteriaBuilder.quot(
                                criteriaBuilder.diff(rentRoot.get("price"), averagePrice), // Utilizar la media calculada aquí
                                averagePrice // Utilizar la media calculada aquí
                        ),
                        100
                ).alias("variation")
        );

        // Definir las restricciones
        List<Predicate> predicates = new ArrayList<>();
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(rentRoot.get("from"), fromDate, toDate));
        }
        if (provinceName != null) {
            predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(provinceJoin.get("name")), provinceName.toLowerCase()));
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        // Agrupar los resultados
        criteriaQuery.groupBy(
                flatJoin.get("id"),
                flatJoin.get("address"),
                provinceJoin.get("name"),
                localityJoin.get("name"),
                rentRoot.get("price")
        );

        // Crear y ejecutar la consulta
        TypedQuery<PriceVariationFromAverageDTO> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }*/
}
