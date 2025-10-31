package com.example.demo.service.humanresource;

import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityRequest;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityResponse;
import com.example.demo.entity.humanresource.IdentityIssuingAuthority;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.humanresource.IdentityIssuingAuthorityMapper;
import com.example.demo.repository.humanresource.IdentityIssuingAuthorityRepository;
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
public class IdentityIssuingAuthorityService {
    final IdentityIssuingAuthorityRepository identityIssuingAuthorityRepository;
    final IdentityIssuingAuthorityMapper identityIssuingAuthorityMapper;

    @Value("${entities.humanresource.identityissuingauthoirity}")
    private String entityName;

    public IdentityIssuingAuthorityResponse createIdentityIssuingAuthority(IdentityIssuingAuthorityRequest request) {
        IdentityIssuingAuthority identityIssuingAuthority = identityIssuingAuthorityMapper.toIdentityIssuingAuthority(request);

        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.save(identityIssuingAuthority));
    }

    public List<IdentityIssuingAuthorityResponse> getIdentityIssuingAuthorities(Pageable pageable) {
        Page<IdentityIssuingAuthority> page = identityIssuingAuthorityRepository.findAll(pageable);
        List<IdentityIssuingAuthorityResponse> dtos = page.getContent()
                .stream().map(identityIssuingAuthorityMapper::toIdentityIssuingAuthorityResponse).toList();
        return dtos;
    }

    public IdentityIssuingAuthorityResponse getIdentityIssuingAuthority(Long id) {
        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName)));
    }

    public IdentityIssuingAuthorityResponse updateIdentityIssuingAuthority(Long id, IdentityIssuingAuthorityRequest request) {
        IdentityIssuingAuthority identityIssuingAuthority = identityIssuingAuthorityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));

        identityIssuingAuthorityMapper.updateIdentityIssuingAuthority(identityIssuingAuthority, request);

        return identityIssuingAuthorityMapper.toIdentityIssuingAuthorityResponse(identityIssuingAuthorityRepository.save(identityIssuingAuthority));
    }

    public void deleteIdentityIssuingAuthority(Long id) {
        IdentityIssuingAuthority identityIssuingAuthority = identityIssuingAuthorityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName));
        identityIssuingAuthorityRepository.deleteById(id);
    }
}
