package com.example.demo.entity.humanresource;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "gender")
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gender_id", nullable = false)
    private Long genderId;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

}