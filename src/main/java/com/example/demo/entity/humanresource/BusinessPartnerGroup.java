package com.example.demo.entity.humanresource;

import com.example.demo.entity.general.SourceSystem;
import com.example.demo.entity.general.UserProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "business_partner_group")
public class BusinessPartnerGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_partner_group_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "business_partner_group_code", length = 50)
    private String businessPartnerGroupCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "source_id", nullable = false, length = 100)
    private String sourceId;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_system_id", nullable = false)
    private SourceSystem sourceSystem;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserProfile createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private UserProfile updatedBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}