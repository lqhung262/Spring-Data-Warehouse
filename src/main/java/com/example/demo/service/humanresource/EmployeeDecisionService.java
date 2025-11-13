package com.example.demo.service.humanresource;


import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.entity.humanresource.EmployeeDecision;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EmployeeDecisionMapper;
import com.example.demo.repository.humanresource.EmployeeDecisionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeDecisionService {
    final EmployeeDecisionRepository employeeDecisionRepository;
    final EmployeeDecisionMapper employeeDecisionMapper;

    @Value("${entities.humanresource.employeedecision}")
    private String entityName;


    public EmployeeDecisionResponse createEmployeeDecision(EmployeeDecisionRequest request) {
        EmployeeDecision employeeDecision = employeeDecisionMapper.toEmployeeDecision(request);

        return employeeDecisionMapper.toEmployeeDecisionResponse(employeeDecisionRepository.save(employeeDecision));
    }

    /**
     * Xử lý Bulk Upsert
     */
    @Transactional
    public List<EmployeeDecisionResponse> bulkUpsertEmployeeDecisions(List<EmployeeDecisionRequest> requests) {

        // Lấy tất cả employeeDecisionCodes từ request
        List<String> employeeDecisionCodes = requests.stream()
                .map(EmployeeDecisionRequest::getEmployeeDecisionCode)
                .toList();

        // Tìm tất cả các employeeDecision đã tồn tại TRONG 1 CÂU QUERY
        Map<String, EmployeeDecision> existingEmployeeDecisionsMap = employeeDecisionRepository.findByEmployeeDecisionCodeIn(employeeDecisionCodes).stream()
                .collect(Collectors.toMap(EmployeeDecision::getEmployeeDecisionCode, employeeDecision -> employeeDecision));

        List<EmployeeDecision> employeeDecisionsToSave = new java.util.ArrayList<>();

        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
        for (EmployeeDecisionRequest request : requests) {
            EmployeeDecision employeeDecision = existingEmployeeDecisionsMap.get(request.getEmployeeDecisionCode());

            if (employeeDecision != null) {
                // --- Logic UPDATE ---
                // EmployeeDecision đã tồn tại -> Cập nhật
                employeeDecisionMapper.updateEmployeeDecision(employeeDecision, request);
                employeeDecisionsToSave.add(employeeDecision);
            } else {
                // --- Logic INSERT ---
                // EmployeeDecision chưa tồn tại -> Tạo mới
                EmployeeDecision newEmployeeDecision = employeeDecisionMapper.toEmployeeDecision(request);
                employeeDecisionsToSave.add(newEmployeeDecision);
            }
        }

        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
        List<EmployeeDecision> savedEmployeeDecisions = employeeDecisionRepository.saveAll(employeeDecisionsToSave);

        // Map sang Response DTO và trả về
        return savedEmployeeDecisions.stream()
                .map(employeeDecisionMapper::toEmployeeDecisionResponse)
                .toList();
    }

    /**
     * Xử lý Bulk Delete
     */
    @Transactional
    public void bulkDeleteEmployeeDecisions(List<Long> ids) {
        // Kiểm tra xem có bao nhiêu ID tồn tại
        long existingCount = employeeDecisionRepository.countByEmployeeDecisionIdIn(ids);
        if (existingCount != ids.size()) {
            // Không phải tất cả ID đều tồn tại
            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
        }

        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
        employeeDecisionRepository.deleteAllById(ids);
    }


    public List<EmployeeDecisionResponse> getEmployeeDecisions(Pageable pageable) {
        return employeeDecisionRepository.findAll(pageable).getContent()
                .stream().map(employeeDecisionMapper::toEmployeeDecisionResponse).toList();
    }

    public EmployeeDecisionResponse getEmployeeDecision(Long id) {
        return employeeDecisionMapper.toEmployeeDecisionResponse(employeeDecisionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EmployeeDecisionResponse updateEmployeeDecision(Long id, EmployeeDecisionRequest request) {
        EmployeeDecision decision = employeeDecisionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        employeeDecisionMapper.updateEmployeeDecision(decision, request);

        return employeeDecisionMapper.toEmployeeDecisionResponse(employeeDecisionRepository.save(decision));
    }

    public void deleteEmployeeDecision(Long employeeDecisionId) {
        EmployeeDecision decision = employeeDecisionRepository.findById(employeeDecisionId)
                .orElseThrow(() -> new NotFoundException(entityName));
        employeeDecisionRepository.deleteById(employeeDecisionId);
    }

}
