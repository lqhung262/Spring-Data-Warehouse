package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả banks tồn tại trong 1 câu query.
     */
    List<Bank> findByBankCodeIn(Collection<String> bankCodes);

    /**
     * Dùng cho Upsert: Tìm 1 bank bằng bankCode
     */
    Optional<Bank> findByBankCode(String bankCode);

    Long countByBankIdIn(Collection<Long> bankIds);
}
