package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.School.SchoolRequest;
import com.example.demo.dto.humanresource.School.SchoolResponse;
import com.example.demo.entity.humanresource.School;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.SchoolMapper;
import com.example.demo.repository.humanresource.SchoolRepository;
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
public class SchoolService {
    final SchoolRepository schoolRepository;
    final SchoolMapper schoolMapper;

    @Value("${entities.humanresource.school}")
    private String entityName;

    public SchoolResponse createSchool(SchoolRequest request) {
        School school = schoolMapper.toSchool(request);

        return schoolMapper.toSchoolResponse(schoolRepository.save(school));
    }

    public List<SchoolResponse> getSchools(Pageable pageable) {
        Page<School> page = schoolRepository.findAll(pageable);
        List<SchoolResponse> dtos = page.getContent()
                .stream().map(schoolMapper::toSchoolResponse).toList();
        return dtos;
    }

    public SchoolResponse getSchool(Long id) {
        return schoolMapper.toSchoolResponse(schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public SchoolResponse updateSchool(Long id, SchoolRequest request) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        schoolMapper.updateSchool(school, request);

        return schoolMapper.toSchoolResponse(schoolRepository.save(school));
    }

    public void deleteSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        schoolRepository.deleteById(id);
    }
}
