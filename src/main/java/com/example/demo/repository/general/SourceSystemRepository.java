package com.example.demo.repository.general;

import com.example.demo.entity.general.SourceSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceSystemRepository extends JpaRepository<SourceSystem, Long> {

}
