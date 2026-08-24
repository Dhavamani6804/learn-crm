package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.request.LeadRequest;
import com.dhava.crmdemo.dto.request.LeadStatusRequest;
import com.dhava.crmdemo.dto.request.LeadToProjectRequest;
import com.dhava.crmdemo.dto.request.ProjectRequest;
import com.dhava.crmdemo.dto.response.LeadResponse;
import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.Lead;
import com.dhava.crmdemo.entity.Project;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.enums.LeadStatus;
import com.dhava.crmdemo.enums.ProjectStatus;
import com.dhava.crmdemo.exception.LeadAlreadyExistException;
import com.dhava.crmdemo.exception.LeadNotFoundException;
import com.dhava.crmdemo.exception.ProjectAlreadyExistException;
import com.dhava.crmdemo.mapper.LeadMapper;
import com.dhava.crmdemo.mapper.ProjectMapper;
import com.dhava.crmdemo.repository.LeadRepository;
import com.dhava.crmdemo.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class LeadService {
    private final LeadRepository leadRepository;
    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final LeadMapper leadMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public LeadResponse createLead(LeadRequest leadRequest) {
        if (leadRepository.existsByEmail(leadRequest.getEmail()) || leadRepository.existsByPhone(leadRequest.getPhone())) {
            throw new LeadAlreadyExistException("Email or Phone already exists");
        }
        Lead lead = new Lead();

        lead.setLeadName(leadRequest.getLeadName());
        lead.setEmail(leadRequest.getEmail());
        lead.setPhone(leadRequest.getPhone());
        lead.setSource(leadRequest.getSource());
        lead.setDescription(leadRequest.getDescription());
        lead.setExpectedBudget(leadRequest.getExpectedBudget());
        lead.setStatus(LeadStatus.NEW);

        Lead createdLead = leadRepository.save(lead);

        activityLogService.logActivity(
                EntityType.LEAD,
                createdLead.getId(),
                ActivityType.CREATE,
                "Lead Created",
                lead.getAssignedUserId(),
                null,
                "Lead " + createdLead.getLeadName() + " has been created"
        );

        return leadMapper.toLeadResponse(createdLead);
    }

    public List<LeadResponse> getAllLeads() {
        return leadRepository.findAll()
                .stream()
                .map(leadMapper::toLeadResponse)
                .toList();
    }

    public LeadResponse getLeadById(Long id) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new LeadNotFoundException("Lead not found"));
        return leadMapper.toLeadResponse(lead);
    }

    @Transactional
    public LeadResponse updateLead(Long id, LeadRequest leadRequest) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new LeadNotFoundException("Lead not found"));

        if (leadRepository.existsByEmailAndIdNot(leadRequest.getEmail(), id)) {
            throw new LeadAlreadyExistException("Email already exists");
        }

        if (leadRepository.existsByPhoneAndIdNot(leadRequest.getPhone(), id)) {
            throw new LeadAlreadyExistException("Phone already exists");
        }

        String oldValue = "Lead Name = " + lead.getLeadName() + " Email = " + lead.getEmail() + " Phone = " + lead.getPhone() + " Source = " + lead.getSource() + " Description = " + lead.getDescription() + " ExpectedBudget = " + lead.getExpectedBudget();

        lead.setLeadName(leadRequest.getLeadName());
        lead.setEmail(leadRequest.getEmail());
        lead.setPhone(leadRequest.getPhone());
        lead.setSource(leadRequest.getSource());
        lead.setDescription(leadRequest.getDescription());
        lead.setExpectedBudget(leadRequest.getExpectedBudget());

        Lead updatedLead = leadRepository.save(lead);

        String newValue = "Lead Name = " + lead.getLeadName() + " Email = " + lead.getEmail() + " Phone = " + lead.getPhone() + " Source = " + lead.getSource() + " Description = " + lead.getDescription() + " ExpectedBudget = " + lead.getExpectedBudget();

        activityLogService.logActivity(
                EntityType.LEAD,
                updatedLead.getId(),
                ActivityType.UPDATE,
                "Lead details updated",
                lead.getAssignedUserId(),
                oldValue,
                newValue
        );

        return leadMapper.toLeadResponse(updatedLead);
    }

    @Transactional
    public void deleteLeadById(Long id) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new LeadNotFoundException("Lead not found"));
        String oldValue = "Lead Name = " + lead.getLeadName() + " Email = " + lead.getEmail() + " Phone = " + lead.getPhone() + " Source = " + lead.getSource() + " Description = " + lead.getDescription() + " ExpectedBudget = " + lead.getExpectedBudget();
        leadRepository.delete(lead);
        activityLogService.logActivity(
                EntityType.LEAD,
                id,
                ActivityType.DELETE,
                "Lead deleted",
                lead.getAssignedUserId(),
                oldValue,
                null
        );
    }

    @Transactional
    public LeadResponse assignUserToLead(Long leadId, Long userId) {

        UserResponse user = userService.getUserById(userId);

        Lead lead = leadRepository.findById(leadId).orElseThrow(() -> new LeadNotFoundException("Lead not found"));

        Long oldAssignedUser = lead.getAssignedUserId();

        lead.setAssignedUserId(userId);
        lead.setStatus(LeadStatus.ASSIGNED);

        Lead updatedLead = leadRepository.save(lead);

        activityLogService.logActivity(
                EntityType.LEAD,
                leadId,
                ActivityType.ASSIGN,
                "Lead assigned to user",
                userId,
                oldAssignedUser == null ? "No user assigned" : oldAssignedUser.toString(),
                user.getName()
        );

        return leadMapper.toLeadResponse(updatedLead);
    }

    @Transactional
    public LeadResponse updateLeadStatus(Long id, LeadStatusRequest request) {

        Lead lead = leadRepository.findById(id).orElseThrow(() -> new LeadNotFoundException("Lead not found"));

        LeadStatus oldStatus = lead.getStatus();

        lead.setStatus(request.getStatus());

        Lead updatedLead = leadRepository.save(lead);

        activityLogService.logActivity(
                EntityType.LEAD,
                id,
                ActivityType.STATUS_CHANGE,
                "Lead status updated",
                lead.getAssignedUserId(),
                oldStatus.name(),
                request.getStatus().name()
        );

        return leadMapper.toLeadResponse(updatedLead);
    }

    @Transactional
    public ProjectResponse leadToProject(Long leadId, LeadToProjectRequest request) {
        Lead lead = leadRepository.findById(leadId).orElseThrow(() -> new LeadNotFoundException("Lead not found"));

        if (lead.getAssignedUserId() == null) {
            throw new IllegalStateException("Lead is not assigned to any user");
        }

        long performedBy = lead.getAssignedUserId();

        if (lead.getStatus() != LeadStatus.QUALIFIED) {
            throw new IllegalStateException("Lead status is not QUALIFIED");
        }
        if (projectRepository.existsByLeadId(leadId)) {
            throw new ProjectAlreadyExistException("Project already exists for this lead");
        }

        Project project = new Project();

        project.setProjectName(request.getProjectName());
        project.setClientName(lead.getLeadName());
        project.setLeadId(leadId);
        project.setDescription(lead.getDescription());
        project.setFinalBudget(lead.getExpectedBudget());
        project.setStatus(ProjectStatus.PLANNED);
        project.setAssignedUserId(performedBy);
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        Project createdProject = projectRepository.save(project);

        LeadStatus oldStatus = lead.getStatus();

        lead.setStatus(LeadStatus.CONVERTED);

        leadRepository.save(lead);

        activityLogService.logActivity(
                EntityType.LEAD,
                lead.getId(),
                ActivityType.CONVERT,
                "Lead converted into project",
                performedBy,
                oldStatus.name(),
                LeadStatus.CONVERTED.name()
        );

        activityLogService.logActivity(
                EntityType.PROJECT,
                createdProject.getId(),
                ActivityType.CREATE,
                "Project created from lead",
                performedBy,
                null,
                "Project " + createdProject.getProjectName() + " created"
        );

        return projectMapper.toProjectResponse(createdProject);
    }
}
