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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dhava.crmdemo.constants.Constants.PROJECT_NOT_FOUND;

@AllArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;
    private final ActivityLogService activityLogService;

    public ProjectResponse createProject(ProjectRequest request) {

        if (request.getAssignedUserId() != null) {
            userService.getUserById(request.getAssignedUserId());
        }

        Project project = getProject(request);

        Project createdProject = projectRepository.save(project);

        activityLogService.logActivity(EntityType.PROJECT, createdProject.getId(), ActivityType.CREATE, "Project created", null, "Project " + createdProject.getProjectName() + " created");

        String assignedUserName = null;

        if (createdProject.getAssignedUserId() != null) {
            assignedUserName = userService.getUserById(createdProject.getAssignedUserId()).getName();
        }

        return projectMapper.toProjectResponse(createdProject, assignedUserName);
    }

    private static Project getProject(ProjectRequest request) {
        return Project.builder().projectName(request.getProjectName()).clientName(request.getClientName()).leadId(request.getLeadId()).description(request.getDescription()).finalBudget(request.getFinalBudget()).assignedUserId(request.getAssignedUserId()).startDate(request.getStartDate()).endDate(request.getEndDate()).status(ProjectStatus.PLANNED).build();
    }

    public ProjectPageResponse getAllProjects(ProjectFilterRequest request) {

        ProjectPageResult result = projectRepository.findAll(request);

        List<Project> projectList = result.getProjects();

        Set<Long> assignedUserIds = projectList.stream().map(Project::getAssignedUserId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, String> userNames = userService.getUserNamesByIds(assignedUserIds);

        List<ProjectResponse> projects = projectList.stream().map(project -> projectMapper.toProjectResponse(project, project.getAssignedUserId() == null ? null : userNames.get(project.getAssignedUserId()))).toList();

        return ProjectPageResponse.builder().content(projects).pageSize(request.getSize()).nextCursor(result.getNextCursor()).hasNext(result.isHasNext()).build();
    }

    public Project returnProjectIfPresent(String projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));
    }

    public ProjectResponse getProjectById(String id) {

        Project project = returnProjectIfPresent(id);
        String assignedUserName = null;

        if (project.getAssignedUserId() != null) {
            assignedUserName = userService.getUserById(project.getAssignedUserId()).getName();
        }

        return projectMapper.toProjectResponse(project, assignedUserName);
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

        String assignedUserName = null;

        if (updatedProject.getAssignedUserId() != null) {
            assignedUserName = userService.getUserById(updatedProject.getAssignedUserId()).getName();
        }

        return projectMapper.toProjectResponse(updatedProject, assignedUserName);
    }

    public void deleteProject(String id) {

        Project project = returnProjectIfPresent(id);

        String oldValue = "name=" + project.getProjectName() + ", client=" + project.getClientName() + ", leadId=" + project.getLeadId() + ", status=" + project.getStatus() + ", finalBudget=" + project.getFinalBudget() + ", assignedUserId=" + project.getAssignedUserId();

        activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.DELETE, "Project deleted", oldValue, null);

        projectRepository.deleteById(id);
    }

    public ProjectResponse assignUserToProject(String projectId, Long userId) {

        Project project = returnProjectIfPresent(projectId);

        UserResponse newUser = userService.getUserById(userId);

        Long oldAssignedUserId = project.getAssignedUserId();

        if (Objects.equals(oldAssignedUserId, userId)) {
            return toProjectResponse(project);
        }

        String oldAssignedUserName = null;

        if (oldAssignedUserId != null) {
            oldAssignedUserName = userService.getUserById(oldAssignedUserId).getName();
        }

        project.setAssignedUserId(userId);

        Project updatedProject = projectRepository.save(project);

        activityLogService.logActivity(EntityType.PROJECT, projectId, ActivityType.ASSIGN, "Project assigned to user", oldAssignedUserName, newUser.getName());

        return toProjectResponse(updatedProject);
    }

    private ProjectResponse toProjectResponse(Project project) {

        String assignedUserName = null;

        if (project.getAssignedUserId() != null) {
            assignedUserName = userService.getUserById(project.getAssignedUserId()).getName();
        }

        return projectMapper.toProjectResponse(project, assignedUserName);
    }

    public ProjectResponse updateProjectStatus(String id, ProjectStatusRequest request) {

        Project project = returnProjectIfPresent(id);

        if (project.getAssignedUserId() == null) {
            throw new NoUserAssignedException("No user is assigned to this project");
        }

        ProjectStatus oldStatus = project.getStatus();

        if (oldStatus == request.getStatus()) {
            return toProjectResponse(project);
        }

        project.setStatus(request.getStatus());

        Project updatedProject = projectRepository.save(project);

        activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.STATUS_CHANGE, "Project status updated", oldStatus.name(), request.getStatus().name());

        return toProjectResponse(updatedProject);
    }

    private boolean hasChanged(Object oldValue, Object newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}