package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldProvinceCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OldProvinceCityRepository extends JpaRepository<OldProvinceCity, Long> {
    Optional<OldProvinceCity> findBySourceId(String sourceId);

    // Count methods for cascade delete checks
    long countByProvinceCity_ProvinceCityId(Long provinceCityId);

    @Query("SELECT opc.provinceCity.provinceCityId, COUNT(opc) FROM OldProvinceCity opc " +
            "WHERE opc.provinceCity.provinceCityId IN :provinceCityIds " +
            "GROUP BY opc.provinceCity.provinceCityId")
    List<Object[]> countByProvinceCityIdIn(@Param("provinceCityIds") List<Long> provinceCityIds);
}
