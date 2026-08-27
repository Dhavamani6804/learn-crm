package com.dhava.crmdemo.enums;

import lombok.Getter;

@Getter
public enum Role {

    SUPER_ADMIN("Super Admin"),
    ADMIN("Admin"),
    USER("User");
    private final String value;
    Role(String value) {
        this.value = value;
    }
}