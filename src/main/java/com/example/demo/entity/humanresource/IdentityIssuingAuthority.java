package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "identity_issuing_authority")
@Data
@SQLDelete(sql = "UPDATE identity_issuing_authority SET is_deleted = true WHERE identity_issuing_authority_id = ?")
@SQLRestriction("is_deleted = false")
public class IdentityIssuingAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identity_issuing_authority_id")
    private Long identityIssuingAuthorityId;

    @Column(name = "source_id", length = 100, unique = true)
    private String sourceId;

    @Column(name = "name", length = 255)
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
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;

}
