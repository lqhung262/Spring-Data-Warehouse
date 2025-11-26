package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "document_type")
public class DocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_type_id", nullable = false)
    private Long documentTypeId;

    @Size(max = 20)
    @NotNull
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

}