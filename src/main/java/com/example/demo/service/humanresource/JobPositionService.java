package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.JobPosition.JobPositionRequest;
import com.example.demo.dto.humanresource.JobPosition.JobPositionResponse;
import com.example.demo.entity.humanresource.JobPosition;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobPositionMapper;
import com.example.demo.repository.humanresource.JobPositionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobPositionService {
    final JobPositionRepository jobPositionRepository;
    final JobPositionMapper jobPositionMapper;

    @Value("${entities.humanresource.jobposition}")
    private String entityName;

    public JobPositionResponse createJobPosition(JobPositionRequest request) {
        JobPosition jobPosition = jobPositionMapper.toJobPosition(request);

        return jobPositionMapper.toJobPositionResponse(jobPositionRepository.save(jobPosition));
    }

    public List<JobPositionResponse> getJobPositions(Pageable pageable) {
        Page<JobPosition> page = jobPositionRepository.findAll(pageable);
        List<JobPositionResponse> dtos = page.getContent()
                .stream().map(jobPositionMapper::toJobPositionResponse).toList();
        return dtos;
    }

    public JobPositionResponse getJobPosition(Long id) {
        return jobPositionMapper.toJobPositionResponse(jobPositionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public JobPositionResponse updateJobPosition(Long id, JobPositionRequest request) {
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        jobPositionMapper.updateJobPosition(jobPosition, request);

        return jobPositionMapper.toJobPositionResponse(jobPositionRepository.save(jobPosition));
    }

    public void deleteJobPosition(Long id) {
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        jobPositionRepository.deleteById(id);
    }
}
