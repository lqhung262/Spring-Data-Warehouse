package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {
    Optional<ExpenseType> findBySourceId(String sourceId);

    Optional<ExpenseType> findByExpenseTypeCode(String code);

    Optional<ExpenseType> findByName(String name);

    // Batch queries for bulk upsert
    List<ExpenseType> findBySourceIdIn(Collection<String> sourceIds);

    List<ExpenseType> findByNameIn(Collection<String> names);

    List<ExpenseType> findByExpenseTypeCodeIn(Collection<String> codes);
}
