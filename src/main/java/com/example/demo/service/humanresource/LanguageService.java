package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Language.LanguageRequest;
import com.example.demo.dto.humanresource.Language.LanguageResponse;
import com.example.demo.entity.humanresource.Language;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.LanguageMapper;
import com.example.demo.repository.humanresource.LanguageRepository;
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
public class LanguageService {
    final LanguageRepository languageRepository;
    final LanguageMapper languageMapper;

    @Value("${entities.humanresource.language}")
    private String entityName;

    public LanguageResponse createLanguage(LanguageRequest request) {
        Language language = languageMapper.toLanguage(request);

        return languageMapper.toLanguageResponse(languageRepository.save(language));
    }

    public List<LanguageResponse> getLanguages(Pageable pageable) {
        Page<Language> page = languageRepository.findAll(pageable);
        List<LanguageResponse> dtos = page.getContent()
                .stream().map(languageMapper::toLanguageResponse).toList();
        return dtos;
    }

    public LanguageResponse getLanguage(Long id) {
        return languageMapper.toLanguageResponse(languageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public LanguageResponse updateLanguage(Long id, LanguageRequest request) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        languageMapper.updateLanguage(language, request);

        return languageMapper.toLanguageResponse(languageRepository.save(language));
    }

    public void deleteLanguage(Long id) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        languageRepository.deleteById(id);
    }
}
