package com.dhava.crmdemo.entity;

import com.dhava.crmdemo.enums.ProjectStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    private String projectName;

    private String clientName;

    private String leadId;

    private String description;

    private BigDecimal finalBudget;

    private ProjectStatus status;

    private Long assignedUserId;

    private LocalDate startDate;

    private LocalDate endDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}