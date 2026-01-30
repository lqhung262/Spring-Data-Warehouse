package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Department.DepartmentRequest;
import com.example.demo.dto.humanresource.Department.DepartmentResponse;
import com.example.demo.entity.humanresource.Department;
import com.example.demo.mapper.common.CommonMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Department entity.
 * Uses CommonMapperConfig for shared settings.
 */
@Mapper(config = CommonMapperConfig.class)
public interface DepartmentMapper {
    
    Department toDepartment(DepartmentRequest request);

    @Mapping(target = "id", source = "departmentId")
    @Mapping(target = "code", source = "departmentCode")
    DepartmentResponse toDepartmentResponse(Department department);

    void updateDepartment(@MappingTarget Department department, DepartmentRequest request);
}