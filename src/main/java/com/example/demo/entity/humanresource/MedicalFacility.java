package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_facility")
@Data
@SQLDelete(sql = "UPDATE medical_facility SET is_deleted = true WHERE medical_facility_id = ?")
@SQLRestriction("is_deleted = false")
public class MedicalFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_facility_id")
    private Long medicalFacilityId;

    @Column(name = "medical_facility_code", length = 50, unique = true)
    private String medicalFacilityCode;

    @Column(name = "source_id", length = 100, unique = true)
    private String sourceId;

    @NotNull
    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @NotNull
    @Column(name = "source_system_id", nullable = false)
    private Long sourceSystemId = 1L;

    @Column(name = "created_by", nullable = false)
    private Long createdBy = 1L;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy = 1L;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_city_id")
    private ProvinceCity provinceCity;

}
