package com.dhava.crmdemo.dto.request;

import com.dhava.crmdemo.enums.LeadStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeadStatusRequest {
    @NotNull
    private LeadStatus status;
}
