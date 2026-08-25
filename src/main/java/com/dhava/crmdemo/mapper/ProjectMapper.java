package com.dhava.crmdemo.mapper;

import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toProjectResponse(Project project) {

        return ProjectResponse.builder().id(project.getId()).projectName(project.getProjectName()).clientName(project.getClientName()).leadId(project.getLeadId()).description(project.getDescription()).finalBudget(project.getFinalBudget()).status(project.getStatus()).assignedUserId(project.getAssignedUserId()).startDate(project.getStartDate()).endDate(project.getEndDate()).createdAt(project.getCreatedAt()).updatedAt(project.getUpdatedAt()).build();

    }
}