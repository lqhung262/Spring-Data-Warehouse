package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "decision_type")
public class DecisionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "decision_type_id", nullable = false)
    private Long decisionTypeId;

    @Size(max = 10)
    @NotNull
    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

}