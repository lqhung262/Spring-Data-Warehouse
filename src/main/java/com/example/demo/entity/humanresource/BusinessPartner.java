package com.example.demo.entity.humanresource;

import com.example.demo.entity.general.Country;
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
@Table(name = "business_partner")
public class BusinessPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "business_partner_id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "business_partner_code", nullable = false, length = 100)
    private String businessPartnerCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "source_id", nullable = false, length = 100)
    private String sourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_partner_group_id")
    private BusinessPartnerGroup businessPartnerGroup;

    @Size(max = 255)
    @NotNull
    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Size(max = 255)
    @Column(name = "full_name")
    private String fullName;

    @Size(max = 100)
    @Column(name = "title", length = 100)
    private String title;

    @Size(max = 255)
    @Column(name = "search_term")
    private String searchTerm;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private ProvinceCity region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @Size(max = 50)
    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Size(max = 50)
    @Column(name = "mobile_phone_number", length = 50)
    private String mobilePhoneNumber;

    @Size(max = 50)
    @Column(name = "fax_no", length = 50)
    private String faxNo;

    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_number_category_id")
    private TaxNumberCategory taxNumberCategory;

    @Size(max = 100)
    @Column(name = "tax_number", length = 100)
    private String taxNumber;

    @Column(name = "id_card_issue_date")
    private Instant idCardIssueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_card_issue_place")
    private IdentityIssuingAuthority idCardIssuePlace;

    @ColumnDefault("0")
    @Column(name = "is_employee")
    private Boolean isEmployee;

    @ColumnDefault("0")
    @Column(name = "is_customer")
    private Boolean isCustomer;

    @ColumnDefault("0")
    @Column(name = "is_vendor")
    private Boolean isVendor;

    @ColumnDefault("0")
    @Column(name = "is_counterparty")
    private Boolean isCounterparty;

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