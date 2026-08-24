package com.dhava.crmdemo.enums;

import lombok.Getter;

@Getter
public enum ActivityType {
    CREATE("Create"),
    UPDATE("Update"),
    DELETE("Delete"),
    STATUS_CHANGE("Status Change"),
    ASSIGN("Assign"),
    CONVERT("Convert"),
    COMPLETED("Completed");

    private final String label;
    ActivityType(String label) {
        this.label = label;
    }
}
