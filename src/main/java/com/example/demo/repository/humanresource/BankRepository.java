package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findBySourceId(String sourceId);

    Optional<Bank> findByBankCode(String code);

    Optional<Bank> findByName(String name);

    // Batch queries for bulk upsert
    List<Bank> findBySourceIdIn(Collection<String> sourceIds);

    List<Bank> findByNameIn(Collection<String> names);

    List<Bank> findByBankCodeIn(Collection<String> codes);
}
