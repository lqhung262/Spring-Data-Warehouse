package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.IdentityIssuingAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityIssuingAuthorityRepository extends JpaRepository<IdentityIssuingAuthority, Long> {
    Optional<IdentityIssuingAuthority> findBySourceId(String sourceId);

    Optional<IdentityIssuingAuthority> findByIdentityIssuingAuthorityCode(String code);

    Optional<IdentityIssuingAuthority> findByName(String name);

    // Batch queries for bulk upsert
    List<IdentityIssuingAuthority> findBySourceIdIn(Collection<String> sourceIds);

    List<IdentityIssuingAuthority> findByNameIn(Collection<String> names);

    List<IdentityIssuingAuthority> findByIdentityIssuingAuthorityCodeIn(Collection<String> codes);
}
