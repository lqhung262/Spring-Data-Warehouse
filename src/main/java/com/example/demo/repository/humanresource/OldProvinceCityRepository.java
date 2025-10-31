package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldProvinceCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldProvinceCityRepository extends JpaRepository<OldProvinceCity, Long> {
}
