-- V1.12__update_permission_urls.sql

-- Update Employee permissions
UPDATE permission
SET url = '/api/v1/human-resource/employees'
WHERE short_name = 'EMPLOYEE_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/employees'
WHERE short_name = 'EMPLOYEE_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/employees'
WHERE short_name = 'EMPLOYEE_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/employees'
WHERE short_name = 'EMPLOYEE_DELETE';

-- Update Department permissions
UPDATE permission
SET url = '/api/v1/human-resource/departments'
WHERE short_name = 'DEPARTMENT_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/departments'
WHERE short_name = 'DEPARTMENT_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/departments'
WHERE short_name = 'DEPARTMENT_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/departments'
WHERE short_name = 'DEPARTMENT_DELETE';

-- Update Bank permissions
UPDATE permission
SET url = '/api/v1/human-resource/banks'
WHERE short_name = 'BANK_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/banks'
WHERE short_name = 'BANK_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/banks'
WHERE short_name = 'BANK_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/banks'
WHERE short_name = 'BANK_DELETE';

-- Update Blood Group permissions
UPDATE permission
SET url = '/api/v1/human-resource/blood-groups'
WHERE short_name = 'BLOODGROUP_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/blood-groups'
WHERE short_name = 'BLOODGROUP_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/blood-groups'
WHERE short_name = 'BLOODGROUP_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/blood-groups'
WHERE short_name = 'BLOODGROUP_DELETE';

-- Update Education Level permissions
UPDATE permission
SET url = '/api/v1/human-resource/education-levels'
WHERE short_name = 'EDUCATIONLEVEL_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/education-levels'
WHERE short_name = 'EDUCATIONLEVEL_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/education-levels'
WHERE short_name = 'EDUCATIONLEVEL_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/education-levels'
WHERE short_name = 'EDUCATIONLEVEL_DELETE';

-- Update Attendance Type permissions
UPDATE permission
SET url = '/api/v1/human-resource/attendance-types'
WHERE short_name = 'ATTENDANCE_TYPE_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/attendance-types'
WHERE short_name = 'ATTENDANCE_TYPE_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/attendance-types'
WHERE short_name = 'ATTENDANCE_TYPE_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/attendance-types'
WHERE short_name = 'ATTENDANCE_TYPE_DELETE';

-- Update Attendance Machine permissions
UPDATE permission
SET url = '/api/v1/human-resource/attendance-machines'
WHERE short_name = 'ATTENDANCE_MACHINE_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/attendance-machines'
WHERE short_name = 'ATTENDANCE_MACHINE_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/attendance-machines'
WHERE short_name = 'ATTENDANCE_MACHINE_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/attendance-machines'
WHERE short_name = 'ATTENDANCE_MACHINE_DELETE';

-- Update Employee Attendance Machine permissions
UPDATE permission
SET url = '/api/v1/human-resource/employee-attendance-machines'
WHERE short_name = 'EMP_ATTEND_MACH_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/employee-attendance-machines'
WHERE short_name = 'EMP_ATTEND_MACH_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-attendance-machines'
WHERE short_name = 'EMP_ATTEND_MACH_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-attendance-machines'
WHERE short_name = 'EMP_ATTEND_MACH_DELETE';

-- Update Employee Education permissions
UPDATE permission
SET url = '/api/v1/human-resource/employee-educations'
WHERE short_name = 'EMPLOYEE_EDU_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/employee-educations'
WHERE short_name = 'EMPLOYEE_EDU_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-educations'
WHERE short_name = 'EMPLOYEE_EDU_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-educations'
WHERE short_name = 'EMPLOYEE_EDU_DELETE';

-- Update Employee Decision permissions
UPDATE permission
SET url = '/api/v1/human-resource/employee-decisions'
WHERE short_name = 'EMPLOYEE_DECISION_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/employee-decisions'
WHERE short_name = 'EMPLOYEE_DECISION_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-decisions'
WHERE short_name = 'EMPLOYEE_DECISION_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-decisions'
WHERE short_name = 'EMPLOYEE_DECISION_DELETE';

-- Update Employee Work Location permissions
UPDATE permission
SET url = '/api/v1/human-resource/employee-work-locations'
WHERE short_name = 'EMP_WORK_LOCATION_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/employee-work-locations'
WHERE short_name = 'EMP_WORK_LOCATION_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-work-locations'
WHERE short_name = 'EMP_WORK_LOCATION_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-work-locations'
WHERE short_name = 'EMP_WORK_LOCATION_DELETE';

-- Update Employee Work Shift permissions
UPDATE permission
SET url = '/api/v1/human-resource/employee-work-shifts'
WHERE short_name = 'EMP_WORK_SHIFT_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/employee-work-shifts'
WHERE short_name = 'EMP_WORK_SHIFT_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-work-shifts'
WHERE short_name = 'EMP_WORK_SHIFT_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-work-shifts'
WHERE short_name = 'EMP_WORK_SHIFT_DELETE';

-- Update Expense Type permissions
UPDATE permission
SET url = '/api/v1/human-resource/expense-types'
WHERE short_name = 'EXPENSE_TYPE_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/expense-types'
WHERE short_name = 'EXPENSE_TYPE_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/expense-types'
WHERE short_name = 'EXPENSE_TYPE_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/expense-types'
WHERE short_name = 'EXPENSE_TYPE_DELETE';

-- Update Employee Type permissions
UPDATE permission
SET url = '/api/v1/human-resource/employee-types'
WHERE short_name = 'EMPLOYEE_TYPE_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/employee-types'
WHERE short_name = 'EMPLOYEE_TYPE_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-types'
WHERE short_name = 'EMPLOYEE_TYPE_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/employee-types'
WHERE short_name = 'EMPLOYEE_TYPE_DELETE';

-- Update Labor Status permissions
UPDATE permission
SET url = '/api/v1/human-resource/labor-statuses'
WHERE short_name = 'LABOR_STATUS_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/labor-statuses'
WHERE short_name = 'LABOR_STATUS_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/labor-statuses'
WHERE short_name = 'LABOR_STATUS_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/labor-statuses'
WHERE short_name = 'LABOR_STATUS_DELETE';

-- Update Job Rank permissions
UPDATE permission
SET url = '/api/v1/human-resource/job-ranks'
WHERE short_name = 'JOB_RANK_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/job-ranks'
WHERE short_name = 'JOB_RANK_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/job-ranks'
WHERE short_name = 'JOB_RANK_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/job-ranks'
WHERE short_name = 'JOB_RANK_DELETE';

-- Update Job Title permissions
UPDATE permission
SET url = '/api/v1/human-resource/job-titles'
WHERE short_name = 'JOB_TITLE_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/job-titles'
WHERE short_name = 'JOB_TITLE_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/job-titles'
WHERE short_name = 'JOB_TITLE_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/job-titles'
WHERE short_name = 'JOB_TITLE_DELETE';

-- Update Job Position permissions
UPDATE permission
SET url = '/api/v1/human-resource/job-positions'
WHERE short_name = 'JOB_POSITION_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/job-positions'
WHERE short_name = 'JOB_POSITION_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/job-positions'
WHERE short_name = 'JOB_POSITION_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/job-positions'
WHERE short_name = 'JOB_POSITION_DELETE';

-- Update Language permissions
UPDATE permission
SET url = '/api/v1/human-resource/languages'
WHERE short_name = 'LANGUAGE_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/languages'
WHERE short_name = 'LANGUAGE_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/languages'
WHERE short_name = 'LANGUAGE_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/languages'
WHERE short_name = 'LANGUAGE_DELETE';

-- Update Nationality permissions
UPDATE permission
SET url = '/api/v1/human-resource/nationalities'
WHERE short_name = 'NATIONALITY_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/nationalities'
WHERE short_name = 'NATIONALITY_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/nationalities'
WHERE short_name = 'NATIONALITY_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/nationalities'
WHERE short_name = 'NATIONALITY_DELETE';

-- Update Marital Status permissions
UPDATE permission
SET url = '/api/v1/human-resource/marital-statuses'
WHERE short_name = 'MARITAL_STATUS_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/marital-statuses'
WHERE short_name = 'MARITAL_STATUS_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/marital-statuses'
WHERE short_name = 'MARITAL_STATUS_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/marital-statuses'
WHERE short_name = 'MARITAL_STATUS_DELETE';

-- Update Major permissions
UPDATE permission
SET url = '/api/v1/human-resource/majors'
WHERE short_name = 'MAJOR_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/majors'
WHERE short_name = 'MAJOR_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/majors'
WHERE short_name = 'MAJOR_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/majors'
WHERE short_name = 'MAJOR_DELETE';

-- Update Specialization permissions
UPDATE permission
SET url = '/api/v1/human-resource/specializations'
WHERE short_name = 'SPECIALIZATION_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/specializations'
WHERE short_name = 'SPECIALIZATION_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/specializations'
WHERE short_name = 'SPECIALIZATION_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/specializations'
WHERE short_name = 'SPECIALIZATION_DELETE';

-- Update School permissions
UPDATE permission
SET url = '/api/v1/human-resource/schools'
WHERE short_name = 'SCHOOL_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/schools'
WHERE short_name = 'SCHOOL_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/schools'
WHERE short_name = 'SCHOOL_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/schools'
WHERE short_name = 'SCHOOL_DELETE';

-- Update Old Ward permissions
UPDATE permission
SET url = '/api/v1/human-resource/old-wards'
WHERE short_name = 'OLD_WARD_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/old-wards'
WHERE short_name = 'OLD_WARD_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/old-wards'
WHERE short_name = 'OLD_WARD_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/old-wards'
WHERE short_name = 'OLD_WARD_DELETE';

-- Update Old District permissions
UPDATE permission
SET url = '/api/v1/human-resource/old-districts'
WHERE short_name = 'OLD_DISTRICT_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/old-districts'
WHERE short_name = 'OLD_DISTRICT_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/old-districts'
WHERE short_name = 'OLD_DISTRICT_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/old-districts'
WHERE short_name = 'OLD_DISTRICT_DELETE';

-- Update Old Province City permissions
UPDATE permission
SET url = '/api/v1/human-resource/old-province-cities'
WHERE short_name = 'OLD_PROVINCE_CITY_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/old-province-cities'
WHERE short_name = 'OLD_PROVINCE_CITY_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/old-province-cities'
WHERE short_name = 'OLD_PROVINCE_CITY_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/old-province-cities'
WHERE short_name = 'OLD_PROVINCE_CITY_DELETE';

-- Update Ward permissions
UPDATE permission
SET url = '/api/v1/human-resource/wards'
WHERE short_name = 'WARD_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/wards'
WHERE short_name = 'WARD_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/wards'
WHERE short_name = 'WARD_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/wards'
WHERE short_name = 'WARD_DELETE';

-- Update Province City permissions
UPDATE permission
SET url = '/api/v1/human-resource/province-cities'
WHERE short_name = 'PROVINCE_CITY_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/province-cities'
WHERE short_name = 'PROVINCE_CITY_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/province-cities'
WHERE short_name = 'PROVINCE_CITY_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/province-cities'
WHERE short_name = 'PROVINCE_CITY_DELETE';

-- Update Identity Issuing Authority permissions
UPDATE permission
SET url = '/api/v1/human-resource/identity-issuing-authorities'
WHERE short_name = 'IDENTITY_ISSUING_AUTHORITY_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/identity-issuing-authorities'
WHERE short_name = 'IDENTITY_ISSUING_AUTHORITY_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/identity-issuing-authorities'
WHERE short_name = 'IDENTITY_ISSUING_AUTHORITY_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/identity-issuing-authorities'
WHERE short_name = 'IDENTITY_ISSUING_AUTHORITY_DELETE';

-- Update Work Shift Group permissions
UPDATE permission
SET url = '/api/v1/human-resource/work-shift-groups'
WHERE short_name = 'WORK_SHIFT_GROUP_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/work-shift-groups'
WHERE short_name = 'WORK_SHIFT_GROUP_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/work-shift-groups'
WHERE short_name = 'WORK_SHIFT_GROUP_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/work-shift-groups'
WHERE short_name = 'WORK_SHIFT_GROUP_DELETE';

-- Update Work Shift permissions
UPDATE permission
SET url = '/api/v1/human-resource/work-shifts'
WHERE short_name = 'WORK_SHIFT_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/work-shifts'
WHERE short_name = 'WORK_SHIFT_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/work-shifts'
WHERE short_name = 'WORK_SHIFT_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/work-shifts'
WHERE short_name = 'WORK_SHIFT_DELETE';

-- Update OT Type permissions
UPDATE permission
SET url = '/api/v1/human-resource/ot-types'
WHERE short_name = 'OT_TYPE_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/ot-types'
WHERE short_name = 'OT_TYPE_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/ot-types'
WHERE short_name = 'OT_TYPE_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/ot-types'
WHERE short_name = 'OT_TYPE_DELETE';

-- Update Medical Facility permissions
UPDATE permission
SET url = '/api/v1/human-resource/medical-facilities'
WHERE short_name = 'MEDICAL_FACILITY_LIST';
UPDATE permission
SET url = '/api/v1/human-resource/medical-facilities'
WHERE short_name = 'MEDICAL_FACILITY_CREATE';
UPDATE permission
SET url = '/api/v1/human-resource/medical-facilities'
WHERE short_name = 'MEDICAL_FACILITY_UPDATE';
UPDATE permission
SET url = '/api/v1/human-resource/medical-facilities'
WHERE short_name = 'MEDICAL_FACILITY_DELETE';

-- Update Work Location permissions (if exist)
UPDATE permission
SET url = '/api/v1/human-resource/work-locations'
WHERE short_name LIKE 'WORK_LOCATION_%';

