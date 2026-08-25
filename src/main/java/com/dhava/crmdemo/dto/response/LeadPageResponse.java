package com.dhava.crmdemo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LeadPageResponse {

    private List<LeadResponse> content;

    private int pageNo;

    private int pageSize;

    private long totalElements;

    private boolean firstPage;

    private boolean lastPage;
}