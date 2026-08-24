package com.dhava.crmdemo.enums;

import lombok.Getter;

@Getter
public enum ProjectStatus {
    PLANNED("Planned"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");
    private final String label;
    ProjectStatus(String label) {
        this.label = label;
    }
}
