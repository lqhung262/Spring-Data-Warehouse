package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Major.MajorRequest;
import com.example.demo.dto.humanresource.Major.MajorResponse;
import com.example.demo.entity.humanresource.Major;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.MajorMapper;
import com.example.demo.repository.humanresource.MajorRepository;
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
public class MajorService {
    final MajorRepository majorRepository;
    final MajorMapper majorMapper;

    @Value("${entities.humanresource.major}")
    private String entityName;

    public MajorResponse createMajor(MajorRequest request) {
        Major major = majorMapper.toMajor(request);

        return majorMapper.toMajorResponse(majorRepository.save(major));
    }

    public List<MajorResponse> getMajors(Pageable pageable) {
        Page<Major> page = majorRepository.findAll(pageable);
        List<MajorResponse> dtos = page.getContent()
                .stream().map(majorMapper::toMajorResponse).toList();
        return dtos;
    }

    public MajorResponse getMajor(Long id) {
        return majorMapper.toMajorResponse(majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public MajorResponse updateMajor(Long id, MajorRequest request) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        majorMapper.updateMajor(major, request);

        return majorMapper.toMajorResponse(majorRepository.save(major));
    }

    public void deleteMajor(Long id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        majorRepository.deleteById(id);
    }
}
