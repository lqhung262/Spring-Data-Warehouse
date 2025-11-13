package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.JobRank.JobRankRequest;
import com.example.demo.dto.humanresource.JobRank.JobRankResponse;
import com.example.demo.entity.humanresource.JobRank;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobRankMapper;
import com.example.demo.repository.humanresource.JobRankRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobRankService {
    final JobRankRepository jobRankRepository;
    final JobRankMapper jobRankMapper;

    @Value("${entities.humanresource.jobrank}")
    private String entityName;

    public JobRankResponse createJobRank(JobRankRequest request) {
        jobRankRepository.findByJobRankCode(request.getJobRankCode()).ifPresent(b -> {
            throw new IllegalArgumentException(entityName + " with job Rank Code " + request.getJobRankCode() + " already exists.");
        });

        JobRank jobRank = jobRankMapper.toJobRank(request);

        return jobRankMapper.toJobRankResponse(jobRankRepository.save(jobRank));
    }

    /**
     * Xử lý Bulk Upsert
     */
    @Transactional
    public List<JobRankResponse> bulkUpsertJobRanks(List<JobRankRequest> requests) {

        // Lấy tất cả jobRankCodes từ request
        List<String> jobRankCodes = requests.stream()
                .map(JobRankRequest::getJobRankCode)
                .toList();

        // Tìm tất cả các jobRank đã tồn tại TRONG 1 CÂU QUERY
        Map<String, JobRank> existingJobRanksMap = jobRankRepository.findByJobRankCodeIn(jobRankCodes).stream()
                .collect(Collectors.toMap(JobRank::getJobRankCode, jobRank -> jobRank));

        List<JobRank> jobRanksToSave = new java.util.ArrayList<>();

        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
        for (JobRankRequest request : requests) {
            JobRank jobRank = existingJobRanksMap.get(request.getJobRankCode());

            if (jobRank != null) {
                // --- Logic UPDATE ---
                // JobRank đã tồn tại -> Cập nhật
                jobRankMapper.updateJobRank(jobRank, request);
                jobRanksToSave.add(jobRank);
            } else {
                // --- Logic INSERT ---
                // JobRank chưa tồn tại -> Tạo mới
                JobRank newJobRank = jobRankMapper.toJobRank(request);
                jobRanksToSave.add(newJobRank);
            }
        }

        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
        List<JobRank> savedJobRanks = jobRankRepository.saveAll(jobRanksToSave);

        // Map sang Response DTO và trả về
        return savedJobRanks.stream()
                .map(jobRankMapper::toJobRankResponse)
                .toList();
    }

    /**
     * Xử lý Bulk Delete
     */
    @Transactional
    public void bulkDeleteJobRanks(List<Long> ids) {
        // Kiểm tra xem có bao nhiêu ID tồn tại
        long existingCount = jobRankRepository.countByJobRankIdIn(ids);
        if (existingCount != ids.size()) {
            // Không phải tất cả ID đều tồn tại
            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
        }

        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
        jobRankRepository.deleteAllById(ids);
    }


    public List<JobRankResponse> getJobRanks(Pageable pageable) {
        Page<JobRank> page = jobRankRepository.findAll(pageable);
        return page.getContent()
                .stream().map(jobRankMapper::toJobRankResponse).toList();
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
