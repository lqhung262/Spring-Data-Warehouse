package com.example.demo.repository.general;

import com.example.demo.entity.general.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findBySourceId(String sourceId);
}
