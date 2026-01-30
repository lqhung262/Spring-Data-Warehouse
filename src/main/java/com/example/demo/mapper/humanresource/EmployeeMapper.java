package com.example.demo.mapper.humanresource;

import com.example.demo.dto.common.RefDto;
import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.entity.humanresource.*;
import com.example.demo.mapper.common.CommonMapperConfig;
import org.mapstruct.*;

/**
 * MapStruct mapper for Employee entity.
 * 
 * Uses CommonMapperConfig for shared settings which includes:
 * - unmappedTargetPolicy = IGNORE (no need for explicit @Mapping(ignore=true))
 * - nullValuePropertyMappingStrategy = IGNORE
 * 
 * Uses RefDto for related entities in response to provide clean, structured data.
 */
@Mapper(config = CommonMapperConfig.class,
        uses = {EmployeeDecisionMapper.class, EmployeeEducationMapper.class, 
                EmployeeWorkShiftMapper.class, EmployeeAttendanceMachineMapper.class, 
                EmployeeWorkLocationMapper.class})
public interface EmployeeMapper {

    /**
     * Convert request DTO to entity.
     * Note: Related entities (gender, bank, etc.) are handled via setReferences() method.
     * The @Mapping(ignore=true) annotations prevent MapStruct from trying to map Long IDs to Entity objects.
     */
    @Mapping(target = "language1", ignore = true)
    @Mapping(target = "language2", ignore = true)
    @Mapping(target = "language3", ignore = true)
    @Mapping(target = "currentAddressWard", ignore = true)
    @Mapping(target = "permanentAddressWard", ignore = true)
    @Mapping(target = "medicalRegistration", ignore = true)
    @Mapping(target = "idIssuePlaceCmnd", ignore = true)
    @Mapping(target = "idIssuePlaceCccd", ignore = true)
    Employee toEmployee(EmployeeRequest request);

    /**
     * Convert entity to response DTO.
     * Uses RefDto for related entities to provide clean, structured data.
     */
    @Mapping(source = "employeeDecisionList", target = "employeeDecisions")
    @Mapping(source = "employeeEducationList", target = "employeeEducations")
    @Mapping(source = "employeeWorkShift", target = "employeeWorkShift")
    @Mapping(source = "employeeAttendanceMachineList", target = "employeeAttendanceMachines")
    @Mapping(source = "employeeWorkLocationList", target = "employeeWorkLocations")
    @Mapping(target = "gender", expression = "java(toRefDto(employee.getGender()))")
    @Mapping(target = "maritalStatus", expression = "java(toMaritalStatusRef(employee.getMaritalStatus()))")
    @Mapping(target = "nationality", expression = "java(toNationalityRef(employee.getNationality()))")
    @Mapping(target = "laborStatus", expression = "java(toLaborStatusRef(employee.getLaborStatus()))")
    @Mapping(target = "bloodGroup", expression = "java(toBloodGroupRef(employee.getBloodGroup()))")
    @Mapping(target = "idIssuePlaceCmnd", expression = "java(toIdentityIssuingAuthorityRef(employee.getIdIssuePlaceCmnd()))")
    @Mapping(target = "idIssuePlaceCccd", expression = "java(toIdentityIssuingAuthorityRef(employee.getIdIssuePlaceCccd()))")
    @Mapping(target = "manager", expression = "java(toManagerRef(employee.getManager()))")
    @Mapping(target = "graduationSchool", expression = "java(toSchoolRef(employee.getGraduationSchool()))")
    @Mapping(target = "language1", expression = "java(toLanguageRef(employee.getLanguage1()))")
    @Mapping(target = "language2", expression = "java(toLanguageRef(employee.getLanguage2()))")
    @Mapping(target = "language3", expression = "java(toLanguageRef(employee.getLanguage3()))")
    @Mapping(target = "currentAddressWard", expression = "java(toWardRef(employee.getCurrentAddressWard()))")
    @Mapping(target = "permanentAddressWard", expression = "java(toWardRef(employee.getPermanentAddressWard()))")
    @Mapping(target = "hometown", expression = "java(toProvinceCityRef(employee.getHometown()))")
    @Mapping(target = "placeOfBirth", expression = "java(toProvinceCityRef(employee.getPlaceOfBirth()))")
    @Mapping(target = "bank", expression = "java(toBankRef(employee.getBank()))")
    @Mapping(target = "medicalRegistration", expression = "java(toMedicalFacilityRef(employee.getMedicalRegistration()))")
    EmployeeResponse toEmployeeResponse(Employee employee);

    /**
     * Update existing entity from request DTO.
     */
    @Mapping(target = "language1", ignore = true)
    @Mapping(target = "language2", ignore = true)
    @Mapping(target = "language3", ignore = true)
    @Mapping(target = "currentAddressWard", ignore = true)
    @Mapping(target = "permanentAddressWard", ignore = true)
    @Mapping(target = "medicalRegistration", ignore = true)
    @Mapping(target = "idIssuePlaceCmnd", ignore = true)
    @Mapping(target = "idIssuePlaceCccd", ignore = true)
    void updateEmployee(@MappingTarget Employee employee, EmployeeRequest request);

    // =====================================================================
    // Helper methods to convert entities to RefDto
    // =====================================================================
    
    default RefDto toRefDto(Gender entity) {
        return entity == null ? null : RefDto.of(entity.getGenderId(), entity.getName());
    }
    
    default RefDto toMaritalStatusRef(MaritalStatus entity) {
        return entity == null ? null : RefDto.of(entity.getMaritalStatusId(), entity.getName());
    }
    
    default RefDto toNationalityRef(Nationality entity) {
        return entity == null ? null : RefDto.of(entity.getNationalityId(), entity.getName());
    }
    
    default RefDto toLaborStatusRef(LaborStatus entity) {
        return entity == null ? null : RefDto.of(entity.getLaborStatusId(), entity.getName());
    }
    
    default RefDto toBloodGroupRef(BloodGroup entity) {
        return entity == null ? null : RefDto.of(entity.getBloodGroupId(), entity.getName());
    }
    
    default RefDto toIdentityIssuingAuthorityRef(IdentityIssuingAuthority entity) {
        return entity == null ? null : RefDto.of(entity.getIdentityIssuingAuthorityId(), entity.getName());
    }
    
    default RefDto toManagerRef(Employee manager) {
        return manager == null ? null : RefDto.of(manager.getId(), manager.getFullName());
    }
    
    default RefDto toSchoolRef(School entity) {
        return entity == null ? null : RefDto.of(entity.getSchoolId(), entity.getName());
    }
    
    default RefDto toLanguageRef(Language entity) {
        return entity == null ? null : RefDto.of(entity.getLanguageId(), entity.getName());
    }
    
    default RefDto toWardRef(Ward entity) {
        return entity == null ? null : RefDto.of(entity.getWardId(), entity.getName());
    }
    
    default RefDto toProvinceCityRef(ProvinceCity entity) {
        return entity == null ? null : RefDto.of(entity.getProvinceCityId(), entity.getName());
    }
    
    default RefDto toBankRef(Bank entity) {
        return entity == null ? null : RefDto.of(entity.getBankId(), entity.getName());
    }
    
    default RefDto toMedicalFacilityRef(MedicalFacility entity) {
        return entity == null ? null : RefDto.of(entity.getMedicalFacilityId(), entity.getName());
    }

    // =====================================================================
    // Methods to set FK references from request IDs to employee entity
    // =====================================================================

    /**
     * Set all FK references from request IDs to employee entity.
     */
    default void setReferences(Employee employee, EmployeeRequest request) {
        setPersonalInfoReferences(employee, request);
        setAddressReferences(employee, request);
        setEducationAndLanguageReferences(employee, request);
        setIdentityReferences(employee, request);
    }

    default void setPersonalInfoReferences(Employee employee, EmployeeRequest request) {
        if (request.getGenderId() != null) {
            Gender gender = new Gender();
            gender.setGenderId(request.getGenderId());
            employee.setGender(gender);
        }
        if (request.getMaritalStatusId() != null) {
            MaritalStatus maritalStatus = new MaritalStatus();
            maritalStatus.setMaritalStatusId(request.getMaritalStatusId());
            employee.setMaritalStatus(maritalStatus);
        }
        if (request.getNationalityId() != null) {
            Nationality nationality = new Nationality();
            nationality.setNationalityId(request.getNationalityId());
            employee.setNationality(nationality);
        }
        if (request.getBloodGroupId() != null) {
            BloodGroup bloodGroup = new BloodGroup();
            bloodGroup.setBloodGroupId(request.getBloodGroupId());
            employee.setBloodGroup(bloodGroup);
        }
    }

    default void setAddressReferences(Employee employee, EmployeeRequest request) {
        if (request.getCurrentAddressWard() != null) {
            Ward ward = new Ward();
            ward.setWardId(request.getCurrentAddressWard());
            employee.setCurrentAddressWard(ward);
        }
        if (request.getPermanentAddressWard() != null) {
            Ward ward = new Ward();
            ward.setWardId(request.getPermanentAddressWard());
            employee.setPermanentAddressWard(ward);
        }
        if (request.getHometownId() != null) {
            ProvinceCity hometown = new ProvinceCity();
            hometown.setProvinceCityId(request.getHometownId());
            employee.setHometown(hometown);
        }
        if (request.getPlaceOfBirthId() != null) {
            ProvinceCity placeOfBirth = new ProvinceCity();
            placeOfBirth.setProvinceCityId(request.getPlaceOfBirthId());
            employee.setPlaceOfBirth(placeOfBirth);
        }
    }

    default void setEducationAndLanguageReferences(Employee employee, EmployeeRequest request) {
        if (request.getGraduationSchoolId() != null) {
            School school = new School();
            school.setSchoolId(request.getGraduationSchoolId());
            employee.setGraduationSchool(school);
        }
        if (request.getLanguage1() != null) {
            Language lang1 = new Language();
            lang1.setLanguageId(request.getLanguage1());
            employee.setLanguage1(lang1);
        }
        if (request.getLanguage2() != null) {
            Language lang2 = new Language();
            lang2.setLanguageId(request.getLanguage2());
            employee.setLanguage2(lang2);
        }
        if (request.getLanguage3() != null) {
            Language lang3 = new Language();
            lang3.setLanguageId(request.getLanguage3());
            employee.setLanguage3(lang3);
        }
    }

    default void setIdentityReferences(Employee employee, EmployeeRequest request) {
        if (request.getIdIssuePlaceCmnd() != null) {
            IdentityIssuingAuthority authority = new IdentityIssuingAuthority();
            authority.setIdentityIssuingAuthorityId(request.getIdIssuePlaceCmnd());
            employee.setIdIssuePlaceCmnd(authority);
        }
        if (request.getIdIssuePlaceCccd() != null) {
            IdentityIssuingAuthority authority = new IdentityIssuingAuthority();
            authority.setIdentityIssuingAuthorityId(request.getIdIssuePlaceCccd());
            employee.setIdIssuePlaceCccd(authority);
        }
        if (request.getBankId() != null) {
            Bank bank = new Bank();
            bank.setBankId(request.getBankId());
            employee.setBank(bank);
        }
        if (request.getLaborStatusId() != null) {
            LaborStatus laborStatus = new LaborStatus();
            laborStatus.setLaborStatusId(request.getLaborStatusId());
            employee.setLaborStatus(laborStatus);
        }
        if (request.getManagerId() != null) {
            Employee manager = new Employee();
            manager.setId(request.getManagerId());
            employee.setManager(manager);
        }
        if (request.getMedicalRegistration() != null) {
            MedicalFacility medicalFacility = new MedicalFacility();
            medicalFacility.setMedicalFacilityId(request.getMedicalRegistration());
            employee.setMedicalRegistration(medicalFacility);
        }
    }
}
