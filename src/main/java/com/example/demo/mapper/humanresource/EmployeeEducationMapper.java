package com.example.demo.mapper.humanresource;

import com.example.demo.dto.common.RefDto;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.entity.humanresource.*;
import com.example.demo.mapper.common.CommonMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for EmployeeEducation entity.
 * Uses CommonMapperConfig for shared settings.
 */
@Mapper(config = CommonMapperConfig.class)
public interface EmployeeEducationMapper {

    EmployeeEducation toEmployeeEducation(EmployeeEducationRequest request);

    @Mapping(target = "id", source = "employeeEducationId")
    @Mapping(target = "major", expression = "java(toMajorRef(education.getMajor()))")
    @Mapping(target = "specialization", expression = "java(toSpecializationRef(education.getSpecialization()))")
    @Mapping(target = "educationLevel", expression = "java(toEducationLevelRef(education.getEducationLevel()))")
    @Mapping(target = "school", expression = "java(toSchoolRef(education.getSchool()))")
    EmployeeEducationResponse toEmployeeEducationResponse(EmployeeEducation education);

    void updateEmployeeEducation(@MappingTarget EmployeeEducation employeeEducation, EmployeeEducationRequest request);

    // Helper methods to convert entities to RefDto
    default RefDto toMajorRef(Major entity) {
        return entity == null ? null : RefDto.of(entity.getMajorId(), entity.getName());
    }
    
    default RefDto toSpecializationRef(Specialization entity) {
        return entity == null ? null : RefDto.of(entity.getSpecializationId(), entity.getName());
    }
    
    default RefDto toEducationLevelRef(EducationLevel entity) {
        return entity == null ? null : RefDto.of(entity.getEducationLevelId(), entity.getName());
    }
    
    default RefDto toSchoolRef(School entity) {
        return entity == null ? null : RefDto.of(entity.getSchoolId(), entity.getName());
    }

    default void setReferences(EmployeeEducation education, EmployeeEducationRequest request) {
        if (request.getMajorId() != null) {
            Major major = new Major();
            major.setMajorId(request.getMajorId());
            education.setMajor(major);
        }
        if (request.getSpecializationId() != null) {
            Specialization specialization = new Specialization();
            specialization.setSpecializationId(request.getSpecializationId());
            education.setSpecialization(specialization);
        }
        if (request.getEducationLevelId() != null) {
            EducationLevel educationLevel = new EducationLevel();
            educationLevel.setEducationLevelId(request.getEducationLevelId());
            education.setEducationLevel(educationLevel);
        }
        if (request.getSchoolId() != null) {
            School school = new School();
            school.setSchoolId(request.getSchoolId());
            education.setSchool(school);
        }
    }
}
