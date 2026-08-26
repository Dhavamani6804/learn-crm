package com.dhava.crmdemo.dto.request;

import com.dhava.crmdemo.enums.ProjectSortBy;
import com.dhava.crmdemo.enums.ProjectStatus;
import com.dhava.crmdemo.enums.SortDirection;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectFilterRequest {

    private int size = 10;

    private String cursor;

    private ProjectSortBy sortBy = ProjectSortBy.CREATED_AT;

    private SortDirection sortDirection = SortDirection.DESC;

    private ProjectStatus status;

    private BigDecimal minBudget;

    private BigDecimal maxBudget;
}