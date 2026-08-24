package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.request.ProjectRequest;
import com.dhava.crmdemo.dto.request.ProjectStatusRequest;
import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.Project;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.enums.ProjectStatus;
import com.dhava.crmdemo.exception.ProjectAlreadyExistException;
import com.dhava.crmdemo.exception.ProjectNotFoundException;
import com.dhava.crmdemo.mapper.ProjectMapper;
import com.dhava.crmdemo.repository.ProjectRepository;
import com.dhava.crmdemo.utils.ProjectSnapshot;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectMapper projectMapper;
    private final ActivityLogService activityLogService;
    private final ProjectSnapshot projectSnapshot;

    @Transactional
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        Project project = new Project();
        project.setProjectName(projectRequest.getProjectName());
        project.setLeadId(projectRequest.getLeadId());
        project.setDescription(projectRequest.getDescription());
        project.setFinalBudget(projectRequest.getFinalBudget());
        project.setAssignedUserId(projectRequest.getAssignedUserId());
        project.setStartDate(projectRequest.getStartDate());
        project.setEndDate(projectRequest.getEndDate());

        Project createdProject = projectRepository.save(project);

        activityLogService.logActivity(
                EntityType.PROJECT,
                createdProject.getId(),
                ActivityType.CREATE,
                "Project created",
                createdProject.getAssignedUserId(),
                null,
                "Project " + createdProject.getProjectName() + " created"
        );

        return projectMapper.toProjectResponse(createdProject);
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toProjectResponse)
                .toList();
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(()->new ProjectNotFoundException("Project not found"));
        return projectMapper.toProjectResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest projectRequest) {
        Project project =  projectRepository.findById(id).orElseThrow(()->new ProjectNotFoundException("Project not found"));

        String oldValue = projectSnapshot.buildProjectSnapshot(project);

        project.setProjectName(projectRequest.getProjectName());
        project.setClientName(projectRequest.getClientName());
        project.setDescription(projectRequest.getDescription());
        project.setFinalBudget(projectRequest.getFinalBudget());
        project.setEndDate(projectRequest.getEndDate());


        if (projectRequest.getAssignedUserId() != null) {
            project.setAssignedUserId(projectRequest.getAssignedUserId());
        }

        Project updatedProject = projectRepository.save(project);

        String newValue = projectSnapshot.buildProjectSnapshot(updatedProject);

        activityLogService.logActivity(
                EntityType.PROJECT,
                updatedProject.getId(),
                ActivityType.UPDATE,
                "Project details updated",
                updatedProject.getAssignedUserId(),
                oldValue,
                newValue
        );

        return projectMapper.toProjectResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(()->new ProjectNotFoundException("Project not found"));

        String oldValue = projectSnapshot.buildProjectSnapshot(project);

        projectRepository.delete(project);

        activityLogService.logActivity(
                EntityType.PROJECT,
                id,
                ActivityType.DELETE,
                "Project deleted",
                project.getAssignedUserId(),
                oldValue,
                null
        );
    }

    @Transactional
    public ProjectResponse assignUserToProject(Long projectId, Long userId) {
        UserResponse user = userService.getUserById(userId);

        Project project = projectRepository.findById(projectId).orElseThrow(()->new ProjectNotFoundException("Project not found"));

        Long oldAssignedUser = project.getAssignedUserId();

        project.setAssignedUserId(userId);
        project.setStatus(ProjectStatus.PLANNED);

        Project updatedProject = projectRepository.save(project);

        activityLogService.logActivity(
          EntityType.PROJECT,
          projectId,
          ActivityType.ASSIGN,
          "Project assigned to user",
          userId,
          oldAssignedUser == null ? "No user assaigned" : oldAssignedUser.toString(),
          user.getName()
        );
        return projectMapper.toProjectResponse(updatedProject);
    }

    @Transactional
    public ProjectResponse updateProjectStatus(Long id, ProjectStatusRequest projectStatusRequest) {

        Project project = projectRepository.findById(id).orElseThrow(()->new ProjectNotFoundException("Project not found"));

        ProjectStatus oldStatus = project.getStatus();

        project.setStatus(projectStatusRequest.getStatus());

        Project updatedProject = projectRepository.save(project);

        activityLogService.logActivity(
                EntityType.PROJECT,
                id,
                ActivityType.STATUS_CHANGE,
                "Project status updated",
                project.getAssignedUserId(),
                oldStatus.name(),
                projectStatusRequest.getStatus().name()
        );
        return projectMapper.toProjectResponse(updatedProject);
    }

}
