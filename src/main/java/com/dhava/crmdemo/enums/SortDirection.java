package com.dhava.crmdemo.enums;

import lombok.Getter;

@Getter
public enum SortDirection {

    ASC("Asc"),
    DESC("Desc");
    private final String value;
    SortDirection(String value) {
        this.value = value;
    }
}