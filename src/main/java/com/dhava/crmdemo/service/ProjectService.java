package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.request.ProjectFilterRequest;
import com.dhava.crmdemo.dto.request.ProjectRequest;
import com.dhava.crmdemo.dto.request.ProjectStatusRequest;
import com.dhava.crmdemo.dto.response.ProjectPageResponse;
import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.Project;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.enums.ProjectStatus;
import com.dhava.crmdemo.exception.NoUserAssignedException;
import com.dhava.crmdemo.exception.ProjectNotFoundException;
import com.dhava.crmdemo.mapper.ProjectMapper;
import com.dhava.crmdemo.repository.ProjectPageResult;
import com.dhava.crmdemo.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.dhava.crmdemo.constants.Constants.PROJECT_NOT_FOUND;

@AllArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;
    private final ActivityLogService activityLogService;

    public ProjectResponse createProject(ProjectRequest request) {

        Project project = getProject(request);

        Project createdProject = projectRepository.save(project);

        activityLogService.logActivity(EntityType.PROJECT, createdProject.getId(), ActivityType.CREATE, "Project created", null, "Project " + createdProject.getProjectName() + " created");

        return projectMapper.toProjectResponse(createdProject);
    }

    private static Project getProject(ProjectRequest request) {
        return Project.builder().projectName(request.getProjectName()).clientName(request.getClientName()).leadId(request.getLeadId()).description(request.getDescription()).finalBudget(request.getFinalBudget()).assignedUserId(request.getAssignedUserId()).startDate(request.getStartDate()).endDate(request.getEndDate()).status(ProjectStatus.PLANNED).build();
    }

    public ProjectPageResponse getAllProjects(ProjectFilterRequest request) {

        ProjectPageResult result = projectRepository.findAll(request);

        List<ProjectResponse> projects = result.getProjects().stream().map(projectMapper::toProjectResponse).toList();

        return ProjectPageResponse.builder().content(projects).pageSize(request.getSize()).nextCursor(result.getNextCursor()).hasNext(result.isHasNext()).build();
    }

    public Project returnProjectIfPresent(String projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));
    }

    public ProjectResponse getProjectById(String id) {

        Project project = returnProjectIfPresent(id);

        return projectMapper.toProjectResponse(project);
    }

    public ProjectResponse updateProject(String id, ProjectRequest request) {

        Project project = returnProjectIfPresent(id);

        if (hasChanged(project.getProjectName(), request.getProjectName())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project name updated", project.getProjectName(), request.getProjectName());

            project.setProjectName(request.getProjectName());
        }

        if (hasChanged(project.getClientName(), request.getClientName())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project client name updated", project.getClientName(), request.getClientName());

            project.setClientName(request.getClientName());
        }

        if (hasChanged(project.getDescription(), request.getDescription())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project description updated", project.getDescription(), request.getDescription());

            project.setDescription(request.getDescription());
        }

        if (hasChanged(project.getFinalBudget(), request.getFinalBudget())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project final budget updated", String.valueOf(project.getFinalBudget()), String.valueOf(request.getFinalBudget()));

            project.setFinalBudget(request.getFinalBudget());
        }

        if (hasChanged(project.getStartDate(), request.getStartDate())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project start date updated", String.valueOf(project.getStartDate()), String.valueOf(request.getStartDate()));

            project.setStartDate(request.getStartDate());
        }

        if (hasChanged(project.getEndDate(), request.getEndDate())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project end date updated", String.valueOf(project.getEndDate()), String.valueOf(request.getEndDate()));

            project.setEndDate(request.getEndDate());
        }

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toProjectResponse(updatedProject);
    }

    public void deleteProject(String id) {

        Project project = returnProjectIfPresent(id);

        String oldValue = "name=" + project.getProjectName() + ", client=" + project.getClientName() + ", leadId=" + project.getLeadId() + ", status=" + project.getStatus() + ", finalBudget=" + project.getFinalBudget() + ", assignedUserId=" + project.getAssignedUserId();

        activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.DELETE, "Project deleted", oldValue, null);

        projectRepository.deleteById(id);
    }

    public ProjectResponse assignUserToProject(String projectId, Long userId) {

        UserResponse user = userService.getUserById(userId);

        Project project = returnProjectIfPresent(projectId);

        Long oldAssignedUser = project.getAssignedUserId();
        UserResponse assignedUser =  userService.getUserById(oldAssignedUser);
        String oldAssignedUserName = assignedUser.getName();

        if (Objects.equals(oldAssignedUser, userId)) {
            return projectMapper.toProjectResponse(project);
        }

        project.setAssignedUserId(userId);

        Project updatedProject = projectRepository.save(project);

        String oldValue = oldAssignedUser == null ? null : oldAssignedUserName;

        String newValue = user.getName();

        activityLogService.logActivity(EntityType.PROJECT, projectId, ActivityType.ASSIGN, "Project assigned to user", oldValue, newValue);
        return projectMapper.toProjectResponse(updatedProject);
    }

    public ProjectResponse updateProjectStatus(String id, ProjectStatusRequest request) {

        Project project = returnProjectIfPresent(id);

        if (project.getAssignedUserId() == null) {
            throw new NoUserAssignedException("No user is assigned to this project");
        }

        ProjectStatus oldStatus = project.getStatus();

        if (oldStatus == request.getStatus()) {
            return projectMapper.toProjectResponse(project);
        }

        project.setStatus(request.getStatus());

        Project updatedProject = projectRepository.save(project);

        activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.STATUS_CHANGE, "Project status updated", oldStatus.name(), request.getStatus().name());

        return projectMapper.toProjectResponse(updatedProject);
    }

    private boolean hasChanged(Object oldValue, Object newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}