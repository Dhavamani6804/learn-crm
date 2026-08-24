package com.dhava.crmdemo.enums;

import lombok.Getter;

@Getter
public enum LeadStatus {
    NEW("New"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    FOLLOW_UP("Follow Up"),
    QUALIFIED("Qualified"),
    REJECTED("Rejected"),
    CONVERTED("Converted");
    private final String label;
    LeadStatus(String label) {
        this.label = label;
    }
}
