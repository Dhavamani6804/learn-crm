package com.dhava.crmdemo.dto.response;

import com.dhava.crmdemo.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ProjectResponse {

    private String id;
    private String projectName;
    private String clientName;
    private String leadId;
    private String description;
    private BigDecimal finalBudget;
    private ProjectStatus status;
    private String assignedUserName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}