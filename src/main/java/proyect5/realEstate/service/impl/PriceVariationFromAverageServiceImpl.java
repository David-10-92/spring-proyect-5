package proyect5.realEstate.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyect5.realEstate.service.dtos.InputDTO;
import proyect5.realEstate.service.dtos.PriceVariationFromAverageDTO;
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
    public List<PriceVariationFromAverageDTO> generateReportNative(InputDTO inputDTO) {
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
                + "    ( 100 * (r.price / (SELECT AVG(f2.price) FROM flats f2 JOIN localities l2 ON f2.locality_id = l2.id JOIN provinces p2 ON l2.province_id = p2.id WHERE p2.name = :provinceName) - 1)) AS variation "
                + "FROM "
                + "    rents r "
                + "    JOIN flats f ON r.flat_id = f.id "
                + "    JOIN localities l ON f.locality_id = l.id "
                + "    JOIN provinces p ON l.province_id = p.id "
                + "WHERE "
                + "    (:fromDate IS NULL OR :toDate IS NULL OR r.from BETWEEN :fromDate AND :toDate) "
                + "    AND (:provinceName IS NULL OR LOWER(p.name) = :provinceName) "
                +
                " ORDER BY r.id ASC";

        // Crear la consulta SQL nativa
        Query query = entityManager.createNativeQuery(sql, "PriceVariationFromAverageMapping");

        // Establecer parámetros si es necesario
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("provinceName", provinceName != null ? provinceName.toLowerCase() : null);

        // Ejecutar la consulta y devolver los resultados
        return query.getResultList();
    }

    @Override
    public List<PriceVariationFromAverageDTO> generateReportCriteria(InputDTO inputDTO) {
        // Obtener los parámetros de entrada desde el objeto InputDTO
        Date fromDate = inputDTO.getFrom();
        Date toDate = inputDTO.getTo();
        String provinceName = inputDTO.getProvince();

        // Crear un CriteriaBuilder para construir la consulta
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Crear un CriteriaQuery con el tipo de resultado deseado
        CriteriaQuery<PriceVariationFromAverageDTO> cq = cb.createQuery(PriceVariationFromAverageDTO.class);

        // Crear un objeto Root para especificar la entidad principal sobre la que se realizará la consulta
        Root<Rent> rentRoot = cq.from(Rent.class);
        cq.orderBy(cb.asc(rentRoot.get("id")));
        // Realizar las joins necesarias para acceder a las entidades relacionadas
        Join<Rent, Flat> flatJoin = rentRoot.join("flat");
        Join<Flat, Locality> localityJoin = flatJoin.join("locality");
        Join<Locality, Province> provinceJoin = localityJoin.join("province");

        // Subconsulta para calcular el precio medio por provincia
        Subquery<Double> avgPriceSubquery = cq.subquery(Double.class);
        Root<Flat> flatRoot = avgPriceSubquery.from(Flat.class);

        Join<Flat, Locality> subLocalityJoin = flatRoot.join("locality");
        Join<Locality, Province> subProvinceJoin = subLocalityJoin.join("province");

        avgPriceSubquery
                .select(cb.avg(flatRoot.get("price")))
                .where(cb.equal(subProvinceJoin.get("name"), provinceName));

        // La variación del precio es 100 * ( (precio_renta / precio_medio) -1 )
        // que da valores negativos para rentas por debajo de la media, y positivos
        // para las superiores.
        Expression<Number> variation = cb.prod(
            cb.sum(cb.quot(rentRoot.get("price"),avgPriceSubquery.getSelection()),-1),
            100
        );

        // Construir la consulta para obtener los datos necesarios
        cq.multiselect(
                flatJoin.get("id").alias("flatId"),
                flatJoin.get("address").alias("street"),
                provinceJoin.get("name").alias("province"),
                localityJoin.get("name").alias("locality"),
                rentRoot.get("price").alias("flatPrice"),
                avgPriceSubquery.getSelection().alias("averagePrice"),
                variation.alias("variation")
        );

        // Aplicar filtros según los parámetros de entrada
        List<Predicate> predicates = new ArrayList<>();
        if (fromDate != null && toDate != null) {
            predicates.add(cb.between(rentRoot.get("from"), fromDate, toDate));
        }
        if (provinceName != null && !provinceName.isEmpty()) {
            predicates.add(cb.equal(cb.lower(provinceJoin.get("name")), provinceName.toLowerCase()));
        }

        // Agregar los predicados al CriteriaQuery
        cq.where(predicates.toArray(new Predicate[0]));

        // Ejecutar la consulta y obtener los resultados
        TypedQuery<PriceVariationFromAverageDTO> query = entityManager.createQuery(cq);
        return query.getResultList();
    }
}
