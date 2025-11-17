package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.JobTitle.JobTitleRequest;
import com.example.demo.dto.humanresource.JobTitle.JobTitleResponse;
import com.example.demo.entity.humanresource.JobTitle;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobTitleMapper;
import com.example.demo.repository.humanresource.JobTitleRepository;
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
public class JobTitleService {
    final JobTitleRepository jobTitleRepository;
    final JobTitleMapper jobTitleMapper;

    @Value("${entities.humanresource.jobtitle}")
    private String entityName;

    public JobTitleResponse createJobTitle(JobTitleRequest request) {
//        jobTitleRepository.findByJobTitleCode(request.getJobTitleCode()).ifPresent(b -> {
//            throw new IllegalArgumentException(entityName + " with job Title Code " + request.getJobTitleCode() + " already exists.");
//        });

        JobTitle jobTitle = jobTitleMapper.toJobTitle(request);

        return jobTitleMapper.toJobTitleResponse(jobTitleRepository.save(jobTitle));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<JobTitleResponse> bulkUpsertJobTitles(List<JobTitleRequest> requests) {
//
//        // Lấy tất cả jobTitleCodes từ request
//        List<String> jobTitleCodes = requests.stream()
//                .map(JobTitleRequest::getJobTitleCode)
//                .toList();
//
//        // Tìm tất cả các jobTitle đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, JobTitle> existingJobTitlesMap = jobTitleRepository.findByJobTitleCodeIn(jobTitleCodes).stream()
//                .collect(Collectors.toMap(JobTitle::getJobTitleCode, jobTitle -> jobTitle));
//
//        List<JobTitle> jobTitlesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (JobTitleRequest request : requests) {
//            JobTitle jobTitle = existingJobTitlesMap.get(request.getJobTitleCode());
//
//            if (jobTitle != null) {
//                // --- Logic UPDATE ---
//                // JobTitle đã tồn tại -> Cập nhật
//                jobTitleMapper.updateJobTitle(jobTitle, request);
//                jobTitlesToSave.add(jobTitle);
//            } else {
//                // --- Logic INSERT ---
//                // JobTitle chưa tồn tại -> Tạo mới
//                JobTitle newJobTitle = jobTitleMapper.toJobTitle(request);
//                jobTitlesToSave.add(newJobTitle);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<JobTitle> savedJobTitles = jobTitleRepository.saveAll(jobTitlesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedJobTitles.stream()
//                .map(jobTitleMapper::toJobTitleResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteJobTitles(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = jobTitleRepository.countByJobTitleIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        jobTitleRepository.deleteAllById(ids);
//    }
    public List<JobTitleResponse> getJobTitles(Pageable pageable) {
        Page<JobTitle> page = jobTitleRepository.findAll(pageable);
        return page.getContent()
                .stream().map(jobTitleMapper::toJobTitleResponse).toList();
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
