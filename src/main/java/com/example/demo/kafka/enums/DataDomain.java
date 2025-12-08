package com.example.demo.kafka.enums;

import lombok.Getter;

@Getter
public enum DataDomain {
    HUMAN_RESOURCE("human-resource"),
    MATERIAL("material"),
    BUSINESS_PARTNER("business-partner"),
    ACCOUNTING("accounting"),
    MANUFACTURING("manufacturing"),
    PROJECT("project"),
    SYSTEM_USER("system-user"),
    ORGANIZATION_STRUCTURE("organization-structure");

    private final String value;

    DataDomain(String value) {
        this.value = value;
    }
}
