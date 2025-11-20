package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeRequest;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeResponse;
import com.example.demo.entity.humanresource.ExpenseType;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.ExpenseTypeMapper;
import com.example.demo.repository.humanresource.ExpenseTypeRepository;
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
public class ExpenseTypeService {
    final ExpenseTypeRepository expenseTypeRepository;
    final ExpenseTypeMapper expenseTypeMapper;

    @Value("${entities.humanresource.expensetype}")
    private String entityName;

    public ExpenseTypeResponse createExpenseType(ExpenseTypeRequest request) {
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            expenseTypeRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        ExpenseType expenseType = expenseTypeMapper.toExpenseType(request);

        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.save(expenseType));
    }

    /**
     * Xử lý Bulk Upsert
     */
//    @Transactional
//    public List<ExpenseTypeResponse> bulkUpsertExpenseTypes(List<ExpenseTypeRequest> requests) {
//
//        // Lấy tất cả expenseTypeCodes từ request
//        List<String> expenseTypeCodes = requests.stream()
//                .map(ExpenseTypeRequest::getExpenseTypeCode)
//                .toList();
//
//        // Tìm tất cả các expenseType đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, ExpenseType> existingExpenseTypesMap = expenseTypeRepository.findByExpenseTypeCodeIn(expenseTypeCodes).stream()
//                .collect(Collectors.toMap(ExpenseType::getExpenseTypeCode, expenseType -> expenseType));
//
//        List<ExpenseType> expenseTypesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (ExpenseTypeRequest request : requests) {
//            ExpenseType expenseType = existingExpenseTypesMap.get(request.getExpenseTypeCode());
//
//            if (expenseType != null) {
//                // --- Logic UPDATE ---
//                // ExpenseType đã tồn tại -> Cập nhật
//                expenseTypeMapper.updateExpenseType(expenseType, request);
//                expenseTypesToSave.add(expenseType);
//            } else {
//                // --- Logic INSERT ---
//                // ExpenseType chưa tồn tại -> Tạo mới
//                ExpenseType newExpenseType = expenseTypeMapper.toExpenseType(request);
//                expenseTypesToSave.add(newExpenseType);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<ExpenseType> savedExpenseTypes = expenseTypeRepository.saveAll(expenseTypesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedExpenseTypes.stream()
//                .map(expenseTypeMapper::toExpenseTypeResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteExpenseTypes(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = expenseTypeRepository.countByExpenseTypeIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        expenseTypeRepository.deleteAllById(ids);
//    }
    public List<ExpenseTypeResponse> getExpenseTypes(Pageable pageable) {
        Page<ExpenseType> page = expenseTypeRepository.findAll(pageable);
        return page.getContent()
                .stream().map(expenseTypeMapper::toExpenseTypeResponse).toList();
    }

    public ExpenseTypeResponse getExpenseType(Long id) {
        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public ExpenseTypeResponse updateExpenseType(Long id, ExpenseTypeRequest request) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            expenseTypeRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getExpenseTypeId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }


        expenseTypeMapper.updateExpenseType(expenseType, request);

        return expenseTypeMapper.toExpenseTypeResponse(expenseTypeRepository.save(expenseType));
    }

    public void deleteExpenseType(Long id) {
        ExpenseType expenseType = expenseTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        expenseTypeRepository.deleteById(id);
    }
}
