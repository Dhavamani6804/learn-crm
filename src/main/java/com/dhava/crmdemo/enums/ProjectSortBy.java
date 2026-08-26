package com.dhava.crmdemo.enums;

import lombok.Getter;

@Getter
public enum ProjectSortBy {

    CREATED_AT("Created At"),
    UPDATED_AT("Updated At"),
    START_DATE("Start Date"),
    END_DATE("End Date");

    private final String value;

    ProjectSortBy(String value) {
        this.value = value;
    }
}
