package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findBySourceId(String sourceId);

    List<Employee> findByIdIn(Collection<Long> ids);

    // Count methods for cascade delete checks
    long countByBank_BankId(Long bankId);

    long countByGender_GenderId(Long genderId);

    long countByMaritalStatus_MaritalStatusId(Long maritalStatusId);

    long countByNationality_NationalityId(Long nationalityId);

    long countByLaborStatus_LaborStatusId(Long laborStatusId);

    long countByGraduationSchool_SchoolId(Long schoolId);

    long countByLanguage1_LanguageId(Long languageId);

    long countByLanguage2_LanguageId(Long languageId);

    long countByLanguage3_LanguageId(Long languageId);

    long countByBloodGroup_BloodGroupId(Long bloodGroupId);

    long countByCurrentAddressWard_WardId(Long wardId);

    long countByPermanentAddressWard_WardId(Long wardId);

    long countByHometown_ProvinceCityId(Long provinceCityId);

    long countByPlaceOfBirth_ProvinceCityId(Long provinceCityId);

    long countByMedicalRegistration_MedicalFacilityId(Long medicalFacilityId);

    long countByIdIssuePlaceCmnd_IdentityIssuingAuthorityId(Long identityIssuingAuthorityId);

    long countByIdIssuePlaceCccd_IdentityIssuingAuthorityId(Long identityIssuingAuthorityId);

    /**
     * Batch count: Đếm employee hometown references
     */
    @Query("SELECT e.hometown.provinceCityId, COUNT(e) FROM Employee e " +
            "WHERE e.hometown.provinceCityId IN :provinceCityIds " +
            "GROUP BY e.hometown.provinceCityId")
    List<Object[]> countHometownByProvinceCityIdIn(@Param("provinceCityIds") List<Long> provinceCityIds);

    /**
     * Batch count: Đếm employee birthplace references
     */
    @Query("SELECT e.placeOfBirth.provinceCityId, COUNT(e) FROM Employee e " +
            "WHERE e.placeOfBirth.provinceCityId IN :provinceCityIds " +
            "GROUP BY e.placeOfBirth. provinceCityId")
    List<Object[]> countPlaceOfBirthByProvinceCityIdIn(@Param("provinceCityIds") List<Long> provinceCityIds);
}

