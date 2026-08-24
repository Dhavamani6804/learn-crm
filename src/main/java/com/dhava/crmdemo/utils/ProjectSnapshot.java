package com.dhava.crmdemo.utils;

import com.dhava.crmdemo.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectSnapshot {
    public String buildProjectSnapshot(Project project) {
        return "Project Name = " + project.getProjectName()
                + ", Client Name = " + project.getClientName()
                + ", Lead ID = " + project.getLeadId()
                + ", Description = " + project.getDescription()
                + ", Final Budget = " + project.getFinalBudget()
                + ", Status = " + project.getStatus()
                + ", Assigned User ID = " + project.getAssignedUserId()
                + ", Start Date = " + project.getStartDate()
                + ", End Date = " + project.getEndDate();
    }
}
