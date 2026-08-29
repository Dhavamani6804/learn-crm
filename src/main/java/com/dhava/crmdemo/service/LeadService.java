package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.request.LeadFilterRequest;
import com.dhava.crmdemo.dto.request.LeadRequest;
import com.dhava.crmdemo.dto.request.LeadStatusRequest;
import com.dhava.crmdemo.dto.request.LeadToProjectRequest;
import com.dhava.crmdemo.dto.response.LeadPageResponse;
import com.dhava.crmdemo.dto.response.LeadResponse;
import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.Lead;
import com.dhava.crmdemo.entity.Project;
import com.dhava.crmdemo.enums.ProjectStatus;
import com.dhava.crmdemo.enums.LeadStatus;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.LeadSortBy;
import com.dhava.crmdemo.enums.SortDirection;
import com.dhava.crmdemo.exception.LeadAlreadyExistException;
import com.dhava.crmdemo.exception.LeadNotFoundException;
import com.dhava.crmdemo.exception.NoUserAssignedException;
import com.dhava.crmdemo.exception.ProjectAlreadyExistException;
import com.dhava.crmdemo.mapper.LeadMapper;
import com.dhava.crmdemo.mapper.ProjectMapper;
import com.dhava.crmdemo.repository.LeadPageResult;
import com.dhava.crmdemo.repository.LeadRepository;
import com.dhava.crmdemo.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.dhava.crmdemo.constants.Constants.LEAD_NOT_FOUND;

@AllArgsConstructor
@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final LeadMapper leadMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public LeadResponse createLead(LeadRequest request) {

        if (leadRepository.existsByEmail(request.getEmail())) {
            throw new LeadAlreadyExistException("Email already exists");
        }

        if (leadRepository.existsByPhone(request.getPhone())) {
            throw new LeadAlreadyExistException("Phone already exists");
        }

        Lead lead = new Lead();

        lead.setLeadName(request.getLeadName());
        lead.setEmail(request.getEmail());
        lead.setPhone(request.getPhone());
        lead.setSource(request.getSource());
        lead.setDescription(request.getDescription());
        lead.setExpectedBudget(request.getExpectedBudget());
        lead.setStatus(LeadStatus.NEW);

        Lead createdLead = leadRepository.save(lead);

        activityLogService.logActivity(EntityType.LEAD, createdLead.getId(), ActivityType.CREATE, "Lead created", null, "Lead " + createdLead.getLeadName() + " created");

        return leadMapper.toLeadResponse(createdLead);
    }

    public LeadPageResponse getAllLeads(LeadFilterRequest request) {

        if (request.getMinBudget() != null && request.getMaxBudget() != null && request.getMinBudget().compareTo(request.getMaxBudget()) > 0) {
            throw new IllegalArgumentException("Minimum budget cannot be greater than maximum budget");
        }

        if (request.getSortBy() == LeadSortBy.ASSIGNED_USER_NAME) {
            return getLeadsSortedByAssignedUserName(request);
        }

        LeadPageResult result = leadRepository.findAll(request.getPageNo(), request.getPageSize(), request.getSortBy(), request.getSortDirection(), request.getSource(), request.getStatus(), request.getMinBudget(), request.getMaxBudget());

        List<LeadResponse> leads = result.getLeads().stream().map(leadMapper::toLeadResponse).toList();

        long totalElements = result.getTotalElements();

        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();

        boolean firstPage = pageNo == 0;
        boolean lastPage = (long) (pageNo + 1) * pageSize >= totalElements;

        return LeadPageResponse.builder().content(leads).pageNo(pageNo).pageSize(pageSize).totalElements(totalElements).firstPage(firstPage).lastPage(lastPage).build();
    }

    private LeadPageResponse getLeadsSortedByAssignedUserName(LeadFilterRequest request) {

        List<Lead> allLeads = leadRepository.findAllForAssignedUserSorting(request.getSource(), request.getStatus(), request.getMinBudget(), request.getMaxBudget());

        allLeads.sort((lead1, lead2) -> {

            String name1 = getAssignedUserName(lead1.getAssignedUserId());
            String name2 = getAssignedUserName(lead2.getAssignedUserId());

            if (name1 == null && name2 == null) {
                return 0;
            }

            if (name1 == null) {
                return 1;
            }

            if (name2 == null) {
                return -1;
            }

            int comparison = name1.compareToIgnoreCase(name2);

            return request.getSortDirection() == SortDirection.ASC ? comparison : -comparison;
        });

        long totalElements = allLeads.size();

        int pageNo = request.getPageNo();
        int pageSize = request.getPageSize();

        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allLeads.size());

        List<Lead> paginatedLeads = fromIndex >= allLeads.size() ? List.of() : allLeads.subList(fromIndex, toIndex);

        List<LeadResponse> responses = paginatedLeads.stream().map(leadMapper::toLeadResponse).toList();

        return LeadPageResponse.builder().content(responses).pageNo(pageNo).pageSize(pageSize).totalElements(totalElements).firstPage(pageNo == 0).lastPage((long) (pageNo + 1) * pageSize >= totalElements).build();
    }

    private String getAssignedUserName(Long userId) {
        if (userId == null) {
            return null;
        }
        UserResponse user = userService.getUserById(userId);
        return user.getName();
    }

    public LeadResponse getLeadById(String id) {
        Lead lead = leadRepository.findById(id).orElseThrow(() -> new LeadNotFoundException(LEAD_NOT_FOUND));
        return leadMapper.toLeadResponse(lead);
    }

    public LeadResponse updateLead(String id, LeadRequest request) {

        Lead lead = leadRepository.findById(id).orElseThrow(() -> new LeadNotFoundException(LEAD_NOT_FOUND));

        if (!Objects.equals(request.getEmail(), lead.getEmail()) && leadRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new LeadAlreadyExistException("Email already exists");
        }

        if (!Objects.equals(request.getPhone(), lead.getPhone()) && leadRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new LeadAlreadyExistException("Phone already exists");
        }

        if (hasChanged(lead.getLeadName(), request.getLeadName())) {
            activityLogService.logActivity(EntityType.LEAD, id, ActivityType.UPDATE, "Lead name updated", lead.getLeadName(), request.getLeadName());
            lead.setLeadName(request.getLeadName());
        }

        if (hasChanged(lead.getEmail(), request.getEmail())) {
            activityLogService.logActivity(EntityType.LEAD, id, ActivityType.UPDATE, "Lead email updated", lead.getEmail(), request.getEmail());
            lead.setEmail(request.getEmail());
        }

        if (hasChanged(lead.getPhone(), request.getPhone())) {
            activityLogService.logActivity(EntityType.LEAD, id, ActivityType.UPDATE, "Lead phone updated", lead.getPhone(), request.getPhone());
            lead.setPhone(request.getPhone());
        }

        if (hasChanged(lead.getSource(), request.getSource())) {
            activityLogService.logActivity(EntityType.LEAD, id, ActivityType.UPDATE, "Lead source updated", lead.getSource(), request.getSource());
            lead.setSource(request.getSource());
        }

        if (hasChanged(lead.getDescription(), request.getDescription())) {
            activityLogService.logActivity(EntityType.LEAD, id, ActivityType.UPDATE, "Lead description updated", lead.getDescription(), request.getDescription());
            lead.setDescription(request.getDescription());
        }

        if (hasChanged(lead.getExpectedBudget(), request.getExpectedBudget())) {
            activityLogService.logActivity(EntityType.LEAD, id, ActivityType.UPDATE, "Lead expected budget updated", String.valueOf(lead.getExpectedBudget()), String.valueOf(request.getExpectedBudget()));
            lead.setExpectedBudget(request.getExpectedBudget());
        }

        Lead updatedLead = leadRepository.save(lead);

        return leadMapper.toLeadResponse(updatedLead);
    }

    public void deleteLeadById(String id) {

        Lead lead = leadRepository.findById(id).orElseThrow(() -> new LeadNotFoundException(LEAD_NOT_FOUND));

        String oldValue = "name=" + lead.getLeadName() + ", email=" + lead.getEmail() + ", phone=" + lead.getPhone() + ", source=" + lead.getSource() + ", status=" + lead.getStatus() + ", expectedBudget=" + lead.getExpectedBudget();

        leadRepository.deleteById(id);

        activityLogService.logActivity(EntityType.LEAD, id, ActivityType.DELETE, "Lead deleted", oldValue, null);
    }

    public LeadResponse assignUserToLead(String leadId, Long userId) {

        Lead lead = leadRepository.findById(leadId).orElseThrow(() -> new LeadNotFoundException(LEAD_NOT_FOUND));
        UserResponse user = userService.getUserById(userId);
        Long oldAssignedUserId = lead.getAssignedUserId();

        if (Objects.equals(oldAssignedUserId, userId)) {
            return leadMapper.toLeadResponse(lead);
        }

        String oldAssignedUserName = null;

        if (oldAssignedUserId != null) {
            UserResponse oldUser = userService.getUserById(oldAssignedUserId);

            oldAssignedUserName = oldUser.getName();
        }

        lead.setAssignedUserId(userId);
        lead.setStatus(LeadStatus.ASSIGNED);

        Lead updatedLead = leadRepository.save(lead);
        String oldValue = oldAssignedUserId == null ? null : oldAssignedUserId + " (" + oldAssignedUserName + ")";
        String newValue = userId + " (" + user.getName() + ")";
        activityLogService.logActivity(EntityType.LEAD, leadId, ActivityType.ASSIGN, "Lead assigned to user", oldValue, newValue);
        return leadMapper.toLeadResponse(updatedLead);
    }

    public LeadResponse updateLeadStatus(String id, LeadStatusRequest request) {

        Lead lead = leadRepository.findById(id).orElseThrow(() -> new LeadNotFoundException(LEAD_NOT_FOUND));

        if (lead.getAssignedUserId() == null) {
            throw new NoUserAssignedException("No user is assigned to this lead");
        }

        LeadStatus oldStatus = lead.getStatus();

        if (oldStatus == request.getStatus()) {
            return leadMapper.toLeadResponse(lead);
        }
        lead.setStatus(request.getStatus());
        Lead updatedLead = leadRepository.save(lead);
        activityLogService.logActivity(EntityType.LEAD, id, ActivityType.STATUS_CHANGE, "Lead status updated", oldStatus.name(), request.getStatus().name());
        return leadMapper.toLeadResponse(updatedLead);
    }

    public ProjectResponse leadToProject(String leadId, LeadToProjectRequest request) {

        Lead lead = leadRepository.findById(leadId).orElseThrow(() -> new LeadNotFoundException(LEAD_NOT_FOUND));

        if (lead.getAssignedUserId() == null) {
            throw new IllegalStateException("Lead is not assigned to any user");
        }

        if (lead.getStatus() != LeadStatus.QUALIFIED) {
            throw new IllegalStateException("Lead status is not QUALIFIED");
        }

        if (projectRepository.existsByLeadId(leadId)) {
            throw new ProjectAlreadyExistException("Project already exists for this lead");
        }

        Project project = getProject(leadId, request, lead);

        Project createdProject = projectRepository.save(project);

        LeadStatus oldStatus = lead.getStatus();

        lead.setStatus(LeadStatus.CONVERTED);
        leadRepository.save(lead);

        activityLogService.logActivity(EntityType.LEAD, leadId, ActivityType.CONVERT, "Lead converted into project", oldStatus.name(), LeadStatus.CONVERTED.name());

        activityLogService.logActivity(EntityType.PROJECT, createdProject.getId(), ActivityType.CREATE, "Project created from lead", null, "Project " + createdProject.getProjectName() + " created");

        String assignedUserName = userService.getUserById(createdProject.getAssignedUserId()).getName();

        return projectMapper.toProjectResponse(createdProject, assignedUserName);
    }

    private static Project getProject(String leadId, LeadToProjectRequest request, Lead lead) {
        Long assignedUserId = lead.getAssignedUserId();

        Project project = new Project();

        project.setProjectName(request.getProjectName());
        project.setClientName(request.getClientName() != null && !request.getClientName().isBlank() ? request.getClientName() : lead.getLeadName());
        project.setLeadId(leadId);
        project.setDescription(request.getDescription() != null ? request.getDescription() : lead.getDescription());
        project.setFinalBudget(request.getFinalBudget() != null ? request.getFinalBudget() : lead.getExpectedBudget());
        project.setStatus(ProjectStatus.PLANNED);
        project.setAssignedUserId(assignedUserId);

        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        return project;
    }

    private boolean hasChanged(Object oldValue, Object newValue) {
        return !Objects.equals(oldValue, newValue);
    }
}
