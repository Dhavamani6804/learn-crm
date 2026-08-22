package com.dhava.crmdemo.mapper;

import com.dhava.crmdemo.dto.response.LeadResponse;
import com.dhava.crmdemo.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public LeadResponse toLeadResponse(Lead lead) {

        LeadResponse leadResponse = new LeadResponse();

        leadResponse.setId(lead.getId());
        leadResponse.setLeadName(lead.getLeadName());
        leadResponse.setEmail(lead.getEmail());
        leadResponse.setPhone(lead.getPhone());
        leadResponse.setSource(lead.getSource());
        leadResponse.setStatus(lead.getStatus());
        leadResponse.setAssignedUserId(lead.getAssignedUserId());
        leadResponse.setDescription(lead.getDescription());
        leadResponse.setExpectedBudget(lead.getExpectedBudget());
        leadResponse.setCreatedAt(lead.getCreatedAt());
        leadResponse.setUpdatedAt(lead.getUpdatedAt());

        return leadResponse;
    }
}
