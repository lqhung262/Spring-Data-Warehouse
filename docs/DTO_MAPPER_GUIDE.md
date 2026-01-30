# DTO/Mapper Refactoring Guide

## Tổng quan các thay đổi

Dự án đã được refactor để cải thiện thiết kế DTO và Mapper theo các nguyên tắc sau:

### 1. Base Classes cho DTOs

#### Request DTOs
- **`BaseRequest`**: Chứa các trường chung cho data warehouse operations:
  - `sourceId`: Unique identifier từ source system
  - `sourceSystemId`: Reference đến source system

- **`BaseCatalogRequest`**: Extends `BaseRequest`, thêm:
  - `code`: Business code
  - `name`: Display name

> **Lưu ý**: Một số entity có tên field code khác nhau (ví dụ: `bankCode`, `departmentCode`). 
> Trong những trường hợp này, extend trực tiếp từ `BaseRequest` và định nghĩa field code riêng.

#### Response DTOs
- **`BaseResponse`**: Chứa `id` (internal ID) - dùng làm reference
- **`BaseCatalogResponse`**: Extends `BaseResponse`, thêm `code` và `name` - dùng làm reference

> **Lưu ý**: Do MapStruct và Lombok `@SuperBuilder` không hoạt động tốt với nhau,
> các Response DTOs sử dụng `@Builder` thay vì kế thừa từ base classes.

### 2. RefDto Pattern

Thay vì trả về cặp `{entityId, entityName}` riêng lẻ:
```java
// TRƯỚC
Long genderId;
String genderName;
Long bankId;
String bankName;
// ... nhiều cặp fields
```

Sử dụng `RefDto` để gom nhóm:
```java
// SAU
RefDto gender;    // { id: 1, name: "Nam" }
RefDto bank;      // { id: 5, name: "Vietcombank" }
```

### 3. CommonMapperConfig

Tập trung cấu hình MapStruct tại một nơi:
```java
@MapperConfig(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommonMapperConfig {}
```

Lợi ích:
- Không cần viết `@Mapping(target = "...", ignore = true)` cho mỗi field không map
- Thống nhất behavior của tất cả mappers

## Cách sử dụng

### Tạo Request DTO mới
```java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyEntityRequest extends BaseRequest {
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    // entity-specific fields...
}
```

### Tạo Response DTO mới
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyEntityResponse {
    
    Long id;
    String code;
    String name;
    
    // Related entities as RefDto
    RefDto department;
    RefDto manager;
    
    // entity-specific fields...
}
```

### Tạo Mapper mới
```java
@Mapper(config = CommonMapperConfig.class)
public interface MyEntityMapper {

    MyEntity toMyEntity(MyEntityRequest request);

    @Mapping(target = "id", source = "myEntityId")
    @Mapping(target = "department", expression = "java(toDepartmentRef(entity.getDepartment()))")
    @Mapping(target = "manager", expression = "java(toManagerRef(entity.getManager()))")
    MyEntityResponse toMyEntityResponse(MyEntity entity);

    void updateMyEntity(@MappingTarget MyEntity entity, MyEntityRequest request);

    // Helper methods for RefDto conversion
    default RefDto toDepartmentRef(Department entity) {
        return entity == null ? null : RefDto.of(entity.getDepartmentId(), entity.getName());
    }
    
    default RefDto toManagerRef(Employee manager) {
        return manager == null ? null : RefDto.of(manager.getId(), manager.getFullName());
    }
}
```

## API Response Example

### Trước refactoring
```json
{
    "id": 1,
    "employeeCode": "EMP001",
    "sourceId": "SRC-001",
    "sourceSystemId": 1,
    "genderId": 1,
    "genderName": "Nam",
    "bankId": 5,
    "bankName": "Vietcombank",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "isDeleted": false
}
```

### Sau refactoring
```json
{
    "id": 1,
    "employeeCode": "EMP001",
    "gender": {
        "id": 1,
        "name": "Nam"
    },
    "bank": {
        "id": 5,
        "name": "Vietcombank"
    }
}
```

Lợi ích:
- API response gọn gàng, dễ đọc
- Không expose internal fields (sourceId, sourceSystemId, audit fields)
- Cấu trúc nested rõ ràng cho related entities

## Các Entity đã được refactor

### DTOs
- Bank (Request/Response)
- Department (Request/Response)
- Employee (Request/Response)
- EmployeeDecision (Response)
- EmployeeEducation (Response)
- EmployeeWorkShift (Response)
- EmployeeAttendanceMachine (Response)
- EmployeeWorkLocation (Response)

### Mappers
- BankMapper
- DepartmentMapper
- EmployeeMapper
- EmployeeDecisionMapper
- EmployeeEducationMapper
- EmployeeWorkShiftMapper
- EmployeeAttendanceMachineMapper
- EmployeeWorkLocationMapper

## Các Entity chưa được refactor

Các entity catalog đơn giản sau có thể được refactor theo pattern tương tự:
- BloodGroup, AttendanceMachine, AttendanceType
- EducationLevel, EmployeeType, ExpenseType
- IdentityIssuingAuthority, JobPosition, JobRank, JobTitle
- LaborStatus, Language, Major, MaritalStatus
- MedicalFacility, Nationality, OldDistrict, OldProvinceCity, OldWard
- OtType, ProvinceCity, School, Specialization
- Ward, WorkLocation, WorkShift, WorkShiftGroup

