package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {EmployeeDecisionMapper.class, EmployeeEducationMapper.class, EmployeeWorkShiftMapper.class, EmployeeAttendanceMachineMapper.class, EmployeeWorkLocationMapper.class})
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeDecisionList", ignore = true)
    @Mapping(target = "employeeEducationList", ignore = true)
    @Mapping(target = "employeeAttendanceMachineList", ignore = true)
    @Mapping(target = "employeeWorkLocationList", ignore = true)
    @Mapping(target = "employeeWorkShift", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "maritalStatus", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "nationality", ignore = true)
    @Mapping(target = "laborStatus", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "graduationSchool", ignore = true)
    @Mapping(target = "language1", ignore = true)
    @Mapping(target = "language2", ignore = true)
    @Mapping(target = "language3", ignore = true)
    @Mapping(target = "bloodGroup", ignore = true)
    @Mapping(target = "currentAddressWard", ignore = true)
    @Mapping(target = "permanentAddressWard", ignore = true)
    @Mapping(target = "hometown", ignore = true)
    @Mapping(target = "placeOfBirth", ignore = true)
    @Mapping(target = "medicalRegistration", ignore = true)
    @Mapping(target = "idIssuePlaceCmnd", ignore = true)
    @Mapping(target = "idIssuePlaceCccd", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Employee toEmployee(EmployeeRequest request);

    @Mapping(source = "employeeDecisionList", target = "employeeDecisions")
    @Mapping(source = "employeeEducationList", target = "employeeEducations")
    @Mapping(source = "employeeWorkShift", target = "employeeWorkShift")
    @Mapping(source = "employeeAttendanceMachineList", target = "employeeAttendanceMachines")
    @Mapping(source = "employeeWorkLocationList", target = "employeeWorkLocations")
    @Mapping(target = "genderId", source = "gender.genderId")
    @Mapping(target = "genderName", source = "gender.name")
    @Mapping(target = "maritalStatusId", source = "maritalStatus.maritalStatusId")
    @Mapping(target = "maritalStatusName", source = "maritalStatus.name")
    @Mapping(target = "idIssuePlaceCmnd", source = "idIssuePlaceCmnd.identityIssuingAuthorityId")
    @Mapping(target = "idIssuePlaceCmndName", source = "idIssuePlaceCmnd.name")
    @Mapping(target = "idIssuePlaceCccd", source = "idIssuePlaceCccd.identityIssuingAuthorityId")
    @Mapping(target = "idIssuePlaceCccdName", source = "idIssuePlaceCccd.name")
    @Mapping(target = "nationalityId", source = "nationality.nationalityId")
    @Mapping(target = "nationalityName", source = "nationality.name")
    @Mapping(target = "laborStatusId", source = "laborStatus.laborStatusId")
    @Mapping(target = "laborStatusName", source = "laborStatus.name")
    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerName", source = "manager.fullName")
    @Mapping(target = "graduationSchoolId", source = "graduationSchool.schoolId")
    @Mapping(target = "graduationSchoolName", source = "graduationSchool.name")
    @Mapping(target = "language1", source = "language1.languageId")
    @Mapping(target = "language1Name", source = "language1.name")
    @Mapping(target = "language2", source = "language2.languageId")
    @Mapping(target = "language2Name", source = "language2.name")
    @Mapping(target = "language3", source = "language3.languageId")
    @Mapping(target = "language3Name", source = "language3.name")
    @Mapping(target = "bloodGroupId", source = "bloodGroup.bloodGroupId")
    @Mapping(target = "bloodGroupName", source = "bloodGroup.name")
    @Mapping(target = "currentAddressWard", source = "currentAddressWard.wardId")
    @Mapping(target = "currentAddressWardName", source = "currentAddressWard.name")
    @Mapping(target = "permanentAddressWard", source = "permanentAddressWard.wardId")
    @Mapping(target = "permanentAddressWardName", source = "permanentAddressWard.name")
    @Mapping(target = "hometownId", source = "hometown.provinceCityId")
    @Mapping(target = "hometownName", source = "hometown.name")
    @Mapping(target = "placeOfBirthId", source = "placeOfBirth.provinceCityId")
    @Mapping(target = "placeOfBirthName", source = "placeOfBirth.name")
    @Mapping(target = "bankId", source = "bank.bankId")
    @Mapping(target = "bankName", source = "bank.name")
    @Mapping(target = "medicalRegistration", source = "medicalRegistration.medicalFacilityId")
    @Mapping(target = "medicalRegistrationName", source = "medicalRegistration.name")
    EmployeeResponse toEmployeeResponse(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeDecisionList", ignore = true)
    @Mapping(target = "employeeEducationList", ignore = true)
    @Mapping(target = "employeeAttendanceMachineList", ignore = true)
    @Mapping(target = "employeeWorkLocationList", ignore = true)
    @Mapping(target = "employeeWorkShift", ignore = true)
    @Mapping(target = "bank", ignore = true)
    @Mapping(target = "maritalStatus", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "nationality", ignore = true)
    @Mapping(target = "laborStatus", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "graduationSchool", ignore = true)
    @Mapping(target = "language1", ignore = true)
    @Mapping(target = "language2", ignore = true)
    @Mapping(target = "language3", ignore = true)
    @Mapping(target = "bloodGroup", ignore = true)
    @Mapping(target = "currentAddressWard", ignore = true)
    @Mapping(target = "permanentAddressWard", ignore = true)
    @Mapping(target = "hometown", ignore = true)
    @Mapping(target = "placeOfBirth", ignore = true)
    @Mapping(target = "medicalRegistration", ignore = true)
    @Mapping(target = "idIssuePlaceCmnd", ignore = true)
    @Mapping(target = "idIssuePlaceCccd", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateEmployee(@MappingTarget Employee employee, EmployeeRequest request);

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
