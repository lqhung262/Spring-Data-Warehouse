package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.JobPosition.JobPositionRequest;
import com.example.demo.dto.humanresource.JobPosition.JobPositionResponse;
import com.example.demo.entity.humanresource.JobPosition;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.JobPositionMapper;
import com.example.demo.repository.humanresource.JobPositionRepository;
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
public class JobPositionService {
    final JobPositionRepository jobPositionRepository;
    final JobPositionMapper jobPositionMapper;

    @Value("${entities.humanresource.jobposition}")
    private String entityName;

    public JobPositionResponse createJobPosition(JobPositionRequest request) {
//        jobPositionRepository.findByJobPositionCode(request.getJobPositionCode()).ifPresent(b -> {
//            throw new IllegalArgumentException(entityName + " with job Position Code " + request.getJobPositionCode() + " already exists.");
//        });

        JobPosition jobPosition = jobPositionMapper.toJobPosition(request);

        return jobPositionMapper.toJobPositionResponse(jobPositionRepository.save(jobPosition));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<JobPositionResponse> bulkUpsertJobPositions(List<JobPositionRequest> requests) {
//
//        // Lấy tất cả jobPositionCodes từ request
//        List<String> jobPositionCodes = requests.stream()
//                .map(JobPositionRequest::getJobPositionCode)
//                .toList();
//
//        // Tìm tất cả các jobPosition đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, JobPosition> existingJobPositionsMap = jobPositionRepository.findByJobPositionCodeIn(jobPositionCodes).stream()
//                .collect(Collectors.toMap(JobPosition::getJobPositionCode, jobPosition -> jobPosition));
//
//        List<JobPosition> jobPositionsToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (JobPositionRequest request : requests) {
//            JobPosition jobPosition = existingJobPositionsMap.get(request.getJobPositionCode());
//
//            if (jobPosition != null) {
//                // --- Logic UPDATE ---
//                // JobPosition đã tồn tại -> Cập nhật
//                jobPositionMapper.updateJobPosition(jobPosition, request);
//                jobPositionsToSave.add(jobPosition);
//            } else {
//                // --- Logic INSERT ---
//                // JobPosition chưa tồn tại -> Tạo mới
//                JobPosition newJobPosition = jobPositionMapper.toJobPosition(request);
//                jobPositionsToSave.add(newJobPosition);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<JobPosition> savedJobPositions = jobPositionRepository.saveAll(jobPositionsToSave);
//
//        // Map sang Response DTO và trả về
//        return savedJobPositions.stream()
//                .map(jobPositionMapper::toJobPositionResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteJobPositions(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = jobPositionRepository.countByJobPositionIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        jobPositionRepository.deleteAllById(ids);
//    }
    public List<JobPositionResponse> getJobPositions(Pageable pageable) {
        Page<JobPosition> page = jobPositionRepository.findAll(pageable);
        return page.getContent()
                .stream().map(jobPositionMapper::toJobPositionResponse).toList();
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
