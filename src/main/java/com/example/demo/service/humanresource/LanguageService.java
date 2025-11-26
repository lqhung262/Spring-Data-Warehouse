package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Language.LanguageRequest;
import com.example.demo.dto.humanresource.Language.LanguageResponse;
import com.example.demo.entity.humanresource.Language;
import com.example.demo.exception.AlreadyExistsException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.LanguageMapper;
import com.example.demo.repository.humanresource.LanguageRepository;
import com.example.demo.repository.humanresource.EmployeeRepository;
import com.example.demo.exception.CannotDeleteException;
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
public class LanguageService {
    final LanguageRepository languageRepository;
    final LanguageMapper languageMapper;
    final EmployeeRepository employeeRepository;

    @Value("${entities.humanresource.language}")
    private String entityName;

    public LanguageResponse createLanguage(LanguageRequest request) {
        // Check source_id uniqueness for create
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            languageRepository.findBySourceId(request.getSourceId()).ifPresent(b -> {
                throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
            });
        }

        Language language = languageMapper.toLanguage(request);

        return languageMapper.toLanguageResponse(languageRepository.save(language));
    }

//    /**
//     * Xử lý Bulk Upsert
//     */
//    @Transactional
//    public List<LanguageResponse> bulkUpsertLanguages(List<LanguageRequest> requests) {
//
//        // Lấy tất cả languageCodes từ request
//        List<String> languageCodes = requests.stream()
//                .map(LanguageRequest::getLanguageCode)
//                .toList();
//
//        // Tìm tất cả các language đã tồn tại TRONG 1 CÂU QUERY
//        Map<String, Language> existingLanguagesMap = languageRepository.findByLanguageCodeIn(languageCodes).stream()
//                .collect(Collectors.toMap(Language::getLanguageCode, language -> language));
//
//        List<Language> languagesToSave = new java.util.ArrayList<>();
//
//        // Lặp qua danh sách request để quyết định UPDATE hay INSERT
//        for (LanguageRequest request : requests) {
//            Language language = existingLanguagesMap.get(request.getLanguageCode());
//
//            if (language != null) {
//                // --- Logic UPDATE ---
//                // Language đã tồn tại -> Cập nhật
//                languageMapper.updateLanguage(language, request);
//                languagesToSave.add(language);
//            } else {
//                // --- Logic INSERT ---
//                // Language chưa tồn tại -> Tạo mới
//                Language newLanguage = languageMapper.toLanguage(request);
//                languagesToSave.add(newLanguage);
//            }
//        }
//
//        // Lưu tất cả (cả insert và update) TRONG 1 LỆNH
//        List<Language> savedLanguages = languageRepository.saveAll(languagesToSave);
//
//        // Map sang Response DTO và trả về
//        return savedLanguages.stream()
//                .map(languageMapper::toLanguageResponse)
//                .toList();
//    }
//
//    /**
//     * Xử lý Bulk Delete
//     */
//    @Transactional
//    public void bulkDeleteLanguages(List<Long> ids) {
//        // Kiểm tra xem có bao nhiêu ID tồn tại
//        long existingCount = languageRepository.countByLanguageIdIn(ids);
//        if (existingCount != ids.size()) {
//            // Không phải tất cả ID đều tồn tại
//            throw new NotFoundException("Some" + entityName + "s not found. Cannot complete bulk delete.");
//        }
//
//        // Xóa tất cả bằng ID trong 1 câu query (hiệu quả)
//        languageRepository.deleteAllById(ids);
//    }


    public List<LanguageResponse> getLanguages(Pageable pageable) {
        Page<Language> page = languageRepository.findAll(pageable);
        return page.getContent()
                .stream().map(languageMapper::toLanguageResponse).toList();
    }

    public LanguageResponse getLanguage(Long id) {
        return languageMapper.toLanguageResponse(languageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public LanguageResponse updateLanguage(Long id, LanguageRequest request) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        // Check source_id uniqueness for update
        if (request.getSourceId() != null && !request.getSourceId().isEmpty()) {
            languageRepository.findBySourceId(request.getSourceId()).ifPresent(existing -> {
                if (!existing.getLanguageId().equals(id)) {
                    throw new AlreadyExistsException(entityName + " with source_id " + request.getSourceId());
                }
            });
        }

        languageMapper.updateLanguage(language, request);

        return languageMapper.toLanguageResponse(languageRepository.save(language));
    }

    public void deleteLanguage(Long id) {
        if (!languageRepository.existsById(id)) {
            throw new NotFoundException(entityName);
        }

        // Check references (RESTRICT strategy) - Language has 3 FK references
        long lang1Count = employeeRepository.countByLanguage1_LanguageId(id);
        long lang2Count = employeeRepository.countByLanguage2_LanguageId(id);
        long lang3Count = employeeRepository.countByLanguage3_LanguageId(id);
        long totalCount = lang1Count + lang2Count + lang3Count;

        if (totalCount > 0) {
            throw new CannotDeleteException(
                    "Language", id, "Employee", totalCount
            );
        }

        languageRepository.deleteById(id);
    }
}
