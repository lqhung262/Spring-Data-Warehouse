package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "ward")
@Data
@SQLDelete(sql = "UPDATE ward SET is_deleted = true WHERE ward_id = ?")
@SQLRestriction("is_deleted = false")
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ward_id")
    private Long wardId;

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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "province_city_id", nullable = false)
    private ProvinceCity provinceCity;

}
