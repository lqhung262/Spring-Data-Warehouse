package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeEducationMapper {

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeEducationId", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "educationLevel", ignore = true)
    @Mapping(target = "school", ignore = true)
    EmployeeEducation toEmployeeEducation(EmployeeEducationRequest request);

    @Mapping(target = "majorId", source = "major.majorId")
    @Mapping(target = "specializationId", source = "specialization.specializationId")
    @Mapping(target = "educationLevelId", source = "educationLevel.educationLevelId")
    @Mapping(target = "schoolId", source = "school.schoolId")
    EmployeeEducationResponse toEmployeeEducationResponse(EmployeeEducation employeeEducation);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeEducationId", ignore = true)
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "educationLevel", ignore = true)
    @Mapping(target = "school", ignore = true)
    void updateEmployeeEducation(@MappingTarget EmployeeEducation employeeEducation, EmployeeEducationRequest request);

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
