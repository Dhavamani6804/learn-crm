package com.dhava.crmdemo.dto.request;

import com.dhava.crmdemo.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectStatusRequest {
    @NotNull
    private ProjectStatus status;

}
