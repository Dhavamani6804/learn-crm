package com.dhava.crmdemo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOwnProfileRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "phone no. is required")
    private String phone;

    private String password;
}