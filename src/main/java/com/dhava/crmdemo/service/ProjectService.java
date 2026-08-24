package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.request.ProjectRequest;
import com.dhava.crmdemo.dto.request.ProjectStatusRequest;
import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.Project;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.enums.ProjectStatus;
import com.dhava.crmdemo.exception.NoUserAssignedException;
import com.dhava.crmdemo.exception.ProjectNotFoundException;
import com.dhava.crmdemo.mapper.ProjectMapper;
import com.dhava.crmdemo.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
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

        activityLogService.logActivity(EntityType.PROJECT, createdProject.getId(), ActivityType.CREATE, "Project created", createdProject.getAssignedUserId(), null, "Project " + createdProject.getProjectName() + " created");

        return projectMapper.toProjectResponse(createdProject);
    }

    private static @NonNull Project getProject(ProjectRequest request) {
        Project project = new Project();

        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName());
        project.setLeadId(request.getLeadId());
        project.setDescription(request.getDescription());
        project.setFinalBudget(request.getFinalBudget());
        project.setAssignedUserId(request.getAssignedUserId());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        project.setStatus(ProjectStatus.PLANNED);
        return project;
    }

    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll().stream().map(projectMapper::toProjectResponse).toList();
    }

    public ProjectResponse getProjectById(String id) {

        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

        return projectMapper.toProjectResponse(project);
    }

    public ProjectResponse updateProject(String id, ProjectRequest request) {

        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

        if (hasChanged(project.getProjectName(), request.getProjectName())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project name updated", project.getAssignedUserId(), project.getProjectName(), request.getProjectName());

            project.setProjectName(request.getProjectName());
        }

        if (hasChanged(project.getClientName(), request.getClientName())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project client name updated", project.getAssignedUserId(), project.getClientName(), request.getClientName());

            project.setClientName(request.getClientName());
        }

        if (hasChanged(project.getDescription(), request.getDescription())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project description updated", project.getAssignedUserId(), project.getDescription(), request.getDescription());

            project.setDescription(request.getDescription());
        }

        if (hasChanged(project.getFinalBudget(), request.getFinalBudget())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project final budget updated", project.getAssignedUserId(), String.valueOf(project.getFinalBudget()), String.valueOf(request.getFinalBudget()));

            project.setFinalBudget(request.getFinalBudget());
        }

        if (hasChanged(project.getStartDate(), request.getStartDate())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project start date updated", project.getAssignedUserId(), String.valueOf(project.getStartDate()), String.valueOf(request.getStartDate()));

            project.setStartDate(request.getStartDate());
        }

        if (hasChanged(project.getEndDate(), request.getEndDate())) {

            activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.UPDATE, "Project end date updated", project.getAssignedUserId(), String.valueOf(project.getEndDate()), String.valueOf(request.getEndDate()));

            project.setEndDate(request.getEndDate());
        }

        Project updatedProject = projectRepository.save(project);

        return projectMapper.toProjectResponse(updatedProject);
    }

    public void deleteProject(String id) {

        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

        activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.DELETE, "Project deleted", project.getAssignedUserId(), project.getProjectName(), null);

        projectRepository.deleteById(id);
    }

    public ProjectResponse assignUserToProject(String projectId, Long userId) {

        UserResponse user = userService.getUserById(userId);

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

        Long oldAssignedUser = project.getAssignedUserId();

        project.setAssignedUserId(userId);

        project.setStatus(ProjectStatus.PLANNED);

        Project updatedProject = projectRepository.save(project);

        activityLogService.logActivity(EntityType.PROJECT, projectId, ActivityType.ASSIGN, "Project assigned to user", userId, oldAssignedUser == null ? null : oldAssignedUser.toString(), user.getName());

        return projectMapper.toProjectResponse(updatedProject);
    }

    public ProjectResponse updateProjectStatus(String id, ProjectStatusRequest request) {

        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));

        if (project.getAssignedUserId() == null) {
            throw new NoUserAssignedException("No user is assigned to this project");
        }

        ProjectStatus oldStatus = project.getStatus();

        if (oldStatus == request.getStatus()) {
            return projectMapper.toProjectResponse(project);
        }

        project.setStatus(request.getStatus());

        Project updatedProject = projectRepository.save(project);

        activityLogService.logActivity(EntityType.PROJECT, id, ActivityType.STATUS_CHANGE, "Project status updated", project.getAssignedUserId(), oldStatus.name(), request.getStatus().name());

        return projectMapper.toProjectResponse(updatedProject);
    }

    private boolean hasChanged(Object oldValue, Object newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}