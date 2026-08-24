package com.dhava.crmdemo.enums;

import lombok.Getter;

@Getter
public enum EntityType {
    USER("User"),
    LEAD("Lead"),
    PROJECT("Project");

    private final String label;
    EntityType(String label) {
        this.label = label;
    }
}
