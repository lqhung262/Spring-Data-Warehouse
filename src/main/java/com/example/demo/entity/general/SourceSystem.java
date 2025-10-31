package com.example.demo.entity.general;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "source_system")
@Data
public class SourceSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "source_system_id")
    private Long sourceSystemId;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
