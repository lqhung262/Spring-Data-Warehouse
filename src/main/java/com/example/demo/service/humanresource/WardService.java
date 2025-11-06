package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.Ward.WardResponse;
import com.example.demo.entity.humanresource.Ward;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.WardMapper;
import com.example.demo.repository.humanresource.WardRepository;
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
public class WardService {
    final WardRepository wardRepository;
    final WardMapper wardMapper;

    @Value("${entities.humanresource.ward}")
    private String entityName;

    public WardResponse createWard(WardRequest request) {
        Ward ward = wardMapper.toWard(request);

        return wardMapper.toWardResponse(wardRepository.save(ward));
    }

    public List<WardResponse> getWards(Pageable pageable) {
        Page<Ward> page = wardRepository.findAll(pageable);
        List<WardResponse> dtos = page.getContent()
                .stream().map(wardMapper::toWardResponse).toList();
        return dtos;
    }

    public WardResponse getWard(Long id) {
        return wardMapper.toWardResponse(wardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public WardResponse updateWard(Long id, WardRequest request) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        wardMapper.updateWard(ward, request);

        return wardMapper.toWardResponse(wardRepository.save(ward));
    }

    public void deleteWard(Long id) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        wardRepository.deleteById(id);
    }
}
