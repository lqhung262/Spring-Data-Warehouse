package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.IdentityIssuingAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentityIssuingAuthorityRepository extends JpaRepository<IdentityIssuingAuthority, Long> {
}
