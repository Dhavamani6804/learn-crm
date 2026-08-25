package com.dhava.crmdemo.enums;

import lombok.Getter;

@Getter
public enum LeadSortBy {

    CREATED_AT("Created At"),
    UPDATED_AT("Updated At"),
    ASSIGNED_USER_NAME("Assigned User Name");
    private final String value;
    LeadSortBy(String value) {
        this.value = value;
    }
}