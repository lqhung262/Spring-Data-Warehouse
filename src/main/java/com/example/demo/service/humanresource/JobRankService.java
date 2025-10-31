package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.JobRank.JobRankRequest;
import com.example.demo.dto.humanresource.JobRank.JobRankResponse;
import com.example.demo.entity.humanresource.JobRank;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobRankMapper;
import com.example.demo.repository.humanresource.JobRankRepository;
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
public class JobRankService {
    final JobRankRepository jobRankRepository;
    final JobRankMapper jobRankMapper;

    @Value("${entities.humanresource.jobrank}")
    private String entityName;

    public JobRankResponse createJobRank(JobRankRequest request) {
        JobRank jobRank = jobRankMapper.toJobRank(request);

        return jobRankMapper.toJobRankResponse(jobRankRepository.save(jobRank));
    }

    public List<JobRankResponse> getJobRanks(Pageable pageable) {
        Page<JobRank> page = jobRankRepository.findAll(pageable);
        List<JobRankResponse> dtos = page.getContent()
                .stream().map(jobRankMapper::toJobRankResponse).toList();
        return dtos;
    }

    public JobRankResponse getJobRank(Long id) {
        return jobRankMapper.toJobRankResponse(jobRankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public JobRankResponse updateJobRank(Long id, JobRankRequest request) {
        JobRank jobRank = jobRankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        jobRankMapper.updateJobRank(jobRank, request);

        return jobRankMapper.toJobRankResponse(jobRankRepository.save(jobRank));
    }

    public void deleteJobRank(Long id) {
        JobRank jobRank = jobRankRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        jobRankRepository.deleteById(id);
    }
}
