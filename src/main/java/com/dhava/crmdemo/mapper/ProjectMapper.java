package com.dhava.crmdemo.mapper;

import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toProjectResponse(Project project) {

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setProjectName(project.getProjectName());
        response.setClientName(project.getClientName());
        response.setLeadId(project.getLeadId());
        response.setDescription(project.getDescription());
        response.setFinalBudget(project.getFinalBudget());
        response.setStatus(project.getStatus());
        response.setAssignedUserId(project.getAssignedUserId());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());

        return response;
    }
}