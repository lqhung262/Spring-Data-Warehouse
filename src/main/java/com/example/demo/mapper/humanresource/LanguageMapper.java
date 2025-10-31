package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Language.LanguageRequest;
import com.example.demo.dto.humanresource.Language.LanguageResponse;
import com.example.demo.entity.humanresource.Language;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LanguageMapper {
    Language toLanguage(LanguageRequest request);

    LanguageResponse toLanguageResponse(Language language);

    void updateLanguage(@MappingTarget Language language, LanguageRequest request);
}