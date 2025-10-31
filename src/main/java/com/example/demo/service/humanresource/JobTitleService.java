package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.JobTitle.JobTitleRequest;
import com.example.demo.dto.humanresource.JobTitle.JobTitleResponse;
import com.example.demo.entity.humanresource.JobTitle;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobTitleMapper;
import com.example.demo.repository.humanresource.JobTitleRepository;
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
public class JobTitleService {
    final JobTitleRepository jobTitleRepository;
    final JobTitleMapper jobTitleMapper;

    @Value("${entities.humanresource.jobtitle}")
    private String entityName;

    public JobTitleResponse createJobTitle(JobTitleRequest request) {
        JobTitle jobTitle = jobTitleMapper.toJobTitle(request);

        return jobTitleMapper.toJobTitleResponse(jobTitleRepository.save(jobTitle));
    }

    public List<JobTitleResponse> getJobTitles(Pageable pageable) {
        Page<JobTitle> page = jobTitleRepository.findAll(pageable);
        List<JobTitleResponse> dtos = page.getContent()
                .stream().map(jobTitleMapper::toJobTitleResponse).toList();
        return dtos;
    }

    public JobTitleResponse getJobTitle(Long id) {
        return jobTitleMapper.toJobTitleResponse(jobTitleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public JobTitleResponse updateJobTitle(Long id, JobTitleRequest request) {
        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        jobTitleMapper.updateJobTitle(jobTitle, request);

        return jobTitleMapper.toJobTitleResponse(jobTitleRepository.save(jobTitle));
    }

    public void deleteJobTitle(Long id) {
        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        jobTitleRepository.deleteById(id);
    }
}
