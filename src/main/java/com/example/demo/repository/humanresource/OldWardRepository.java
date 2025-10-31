package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OldWardRepository extends JpaRepository<OldWard, Long> {
}
