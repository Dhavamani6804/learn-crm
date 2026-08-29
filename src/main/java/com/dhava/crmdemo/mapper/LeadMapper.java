package com.dhava.crmdemo.mapper;

import com.dhava.crmdemo.dto.response.LeadResponse;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.Lead;
import com.dhava.crmdemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadMapper {

    private final UserService userService;

    public LeadResponse toLeadResponse(Lead lead) {

        LeadResponse leadResponse = new LeadResponse();

        leadResponse.setId(lead.getId());
        leadResponse.setLeadName(lead.getLeadName());
        leadResponse.setEmail(lead.getEmail());
        leadResponse.setPhone(lead.getPhone());
        leadResponse.setSource(lead.getSource());
        leadResponse.setStatus(lead.getStatus());

        if (lead.getAssignedUserId() != null) {
            UserResponse user = userService.getUserById(lead.getAssignedUserId());

            leadResponse.setAssignedUserName(user.getName());
        }

        leadResponse.setDescription(lead.getDescription());
        leadResponse.setExpectedBudget(lead.getExpectedBudget());
        leadResponse.setCreatedAt(lead.getCreatedAt());
        leadResponse.setUpdatedAt(lead.getUpdatedAt());

        return leadResponse;
    }
}