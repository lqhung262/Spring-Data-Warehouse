package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeEducationRepository extends JpaRepository<EmployeeEducation, Long> {
    Optional<EmployeeEducation> findByEmployee_IdAndMajor_MajorIdAndSpecialization_SpecializationIdAndEducationLevel_EducationLevelIdAndSchool_SchoolId(
            Long employeeId,
            Long majorId,
            Long specializationId,
            Long educationLevelId,
            Long schoolId
    );

    List<EmployeeEducation> findByEmployee_Id(Long employeeId);

    // Count methods for cascade delete checks
    long countByMajor_MajorId(Long majorId);

    long countBySpecialization_SpecializationId(Long specializationId);

    long countByEducationLevel_EducationLevelId(Long educationLevelId);

    long countBySchool_SchoolId(Long schoolId);
}
