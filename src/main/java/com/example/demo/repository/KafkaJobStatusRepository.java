package com.example.demo.repository;

import com.example.demo.entity.KafkaJobStatus;
import com.example.demo.kafka.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KafkaJobStatusRepository extends JpaRepository<KafkaJobStatus, Long> {

    Optional<KafkaJobStatus> findByJobId(String jobId);

    List<KafkaJobStatus> findByStatus(JobStatus status);

    List<KafkaJobStatus> findByStatusAndCreatedAtBefore(JobStatus status, LocalDateTime dateTime);
}