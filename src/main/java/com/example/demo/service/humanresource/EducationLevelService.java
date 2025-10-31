package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.EducationLevel.EducationLevelRequest;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelResponse;
import com.example.demo.entity.humanresource.EducationLevel;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.EducationLevelMapper;
import com.example.demo.repository.humanresource.EducationLevelRepository;
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
public class EducationLevelService {
    final EducationLevelRepository educationLevelRepository;
    final EducationLevelMapper educationLevelMapper;

    @Value("${entities.humanresource.educationlevel}")
    private String entityName;

    public EducationLevelResponse createEducationLevel(EducationLevelRequest request) {
        EducationLevel educationLevel = educationLevelMapper.toEducationLevel(request);

        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.save(educationLevel));
    }

    public List<EducationLevelResponse> getEducationLevels(Pageable pageable) {
        Page<EducationLevel> page = educationLevelRepository.findAll(pageable);
        List<EducationLevelResponse> dtos = page.getContent()
                .stream().map(educationLevelMapper::toEducationLevelResponse).toList();
        return dtos;
    }

    public EducationLevelResponse getEducationLevel(Long id) {
        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public EducationLevelResponse updateEducationLevel(Long id, EducationLevelRequest request) {
        EducationLevel educationLevel = educationLevelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        educationLevelMapper.updateEducationLevel(educationLevel, request);

        return educationLevelMapper.toEducationLevelResponse(educationLevelRepository.save(educationLevel));
    }

    public void deleteEducationLevel(Long id) {
        EducationLevel educationLevel = educationLevelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        educationLevelRepository.deleteById(id);
    }
}
