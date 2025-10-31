package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldDistrictRepository extends JpaRepository<OldDistrict, Long> {
}
