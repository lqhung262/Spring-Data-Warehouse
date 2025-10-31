package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtTypeRepository extends JpaRepository<OtType, Long> {
}
