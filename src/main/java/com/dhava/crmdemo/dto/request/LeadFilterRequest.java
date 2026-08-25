package com.dhava.crmdemo.dto.request;

import com.dhava.crmdemo.enums.LeadSortBy;
import com.dhava.crmdemo.enums.LeadStatus;
import com.dhava.crmdemo.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeadFilterRequest {

    @Min(value = 0,message = "page number cannot be negative")
    private Integer pageNo = 0;

    @Min(value = 0,message = "page number cannot be negative")
    @Max(value = 100,message = "page number cannot exceed 100")
    private Integer pageSize = 10;

    private LeadSortBy sortBy = LeadSortBy.CREATED_AT;

    private SortDirection sortDirection = SortDirection.DESC;

    private String source;

    private LeadStatus status;

    private BigDecimal minBudget;

    private BigDecimal maxBudget;
}
