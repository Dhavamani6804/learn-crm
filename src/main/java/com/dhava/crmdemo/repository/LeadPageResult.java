package com.dhava.crmdemo.repository;

import com.dhava.crmdemo.entity.Lead;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LeadPageResult {

    private List<Lead> leads;

    private long totalElements;
}