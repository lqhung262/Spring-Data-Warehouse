package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.JobRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRankRepository extends JpaRepository<JobRank, Long> {
}
