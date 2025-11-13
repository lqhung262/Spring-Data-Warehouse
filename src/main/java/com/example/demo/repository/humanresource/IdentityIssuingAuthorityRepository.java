package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.IdentityIssuingAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityIssuingAuthorityRepository extends JpaRepository<IdentityIssuingAuthority, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả IdentityIssuingAuthoritys tồn tại trong 1 câu query.
     */
    List<IdentityIssuingAuthority> findByIdentityIssuingAuthorityCodeIn(Collection<String> identityIssuingAuthorityCodes);

    /**
     * Dùng cho Upsert: Tìm 1 IdentityIssuingAuthority bằng identityIssuingAuthorityCode
     */
    Optional<IdentityIssuingAuthority> findByIdentityIssuingAuthorityId(Long identityIssuingAuthorityCode);

    Long countByIdentityIssuingAuthorityIdIn(Collection<Long> identityIssuingAuthorityIds);
}
