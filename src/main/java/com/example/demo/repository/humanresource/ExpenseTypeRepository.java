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
//    /**
//     * Tối ưu cho Upsert: Tìm tất cả expenseTypes tồn tại trong 1 câu query.
//     */
//    List<ExpenseType> findByExpenseTypeCodeIn(Collection<String> expenseTypeCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 expenseType bằng expenseTypeCode
//     */
//    Optional<ExpenseType> findByExpenseTypeCode(String expenseTypeCode);
//
//    Long countByExpenseTypeIdIn(Collection<Long> expenseTypeIds);
}
