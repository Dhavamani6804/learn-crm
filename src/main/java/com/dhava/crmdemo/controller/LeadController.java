package com.dhava.crmdemo.controller;

import com.dhava.crmdemo.api.ApiResponse;
import com.dhava.crmdemo.dto.request.LeadFilterRequest;
import com.dhava.crmdemo.dto.request.LeadRequest;
import com.dhava.crmdemo.dto.request.LeadStatusRequest;
import com.dhava.crmdemo.dto.request.LeadToProjectRequest;
import com.dhava.crmdemo.dto.response.LeadPageResponse;
import com.dhava.crmdemo.dto.response.LeadResponse;
import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.service.LeadService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller responsible for managing leads.
 *
 * <p>Provides endpoints for creating, retrieving, updating and deleting leads.
 * It also supports assigning leads to users, updating lead status, and
 * converting qualified leads into projects.</p>
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    /**
     * Creates a new lead.
     *
     * <p>The request is validated before being passed to the service layer.
     * If the lead is successfully created, the API returns HTTP 201 (Created).</p>
     *
     * @param request the lead details required to create a new lead
     * @return a response containing the newly created lead
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(@Valid @RequestBody LeadRequest request) {

        LeadResponse response = leadService.createLead(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response, "Lead created successfully"));
    }

    /**
     * Retrieves all leads.
     *
     * @return a response containing a list of all leads
     */
    @GetMapping
    public ResponseEntity<ApiResponse<LeadPageResponse>> getAllLeads(@Valid @ModelAttribute LeadFilterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.getAllLeads(request), "Leads fetched successfully"));
    }

    /**
     * Retrieves a lead by its unique identifier.
     *
     * @param id the unique identifier of the lead
     * @return a response containing the requested lead
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadById(@PathVariable String id) {

        return ResponseEntity.ok(ApiResponse.ok(leadService.getLeadById(id), "Lead fetched successfully"));
    }

    /**
     * Updates the details of an existing lead.
     *
     * <p>The supplied request is validated before updating the lead.
     * Activity logs are generated for individual field changes.</p>
     *
     * @param id      the unique identifier of the lead to update
     * @param request the updated lead details
     * @return a response containing the updated lead
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(@PathVariable String id, @Valid @RequestBody LeadRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(leadService.updateLead(id, request), "Lead updated successfully"));
    }

    /**
     * Deletes a lead by its unique identifier.
     *
     * @param id the unique identifier of the lead to delete
     * @return a response indicating that the lead was deleted successfully
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable String id) {

        leadService.deleteLeadById(id);

        return ResponseEntity.ok(ApiResponse.noContent("Lead deleted successfully"));
    }

    /**
     * Assigns a lead to a user.
     *
     * <p>Assigning a lead also updates its status to {@code ASSIGNED}.
     * The assigned user must exist in the system.</p>
     *
     * @param id     the unique identifier of the lead
     * @param userId the unique identifier of the user to whom the lead is assigned
     * @return a response containing the updated lead
     */
    @PatchMapping("/{id}/assign/{userId}")
    public ResponseEntity<ApiResponse<LeadResponse>> assignUserToLead(@PathVariable String id, @PathVariable Long userId) {

        return ResponseEntity.ok(ApiResponse.ok(leadService.assignUserToLead(id, userId), "Lead assigned to user successfully"));
    }

    /**
     * Updates the status of a lead.
     *
     * <p>The lead must be assigned to a user before its status can be changed.</p>
     *
     * @param id      the unique identifier of the lead
     * @param request the request containing the new lead status
     * @return a response containing the updated lead
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLeadStatus(@PathVariable String id, @Valid @RequestBody LeadStatusRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(leadService.updateLeadStatus(id, request), "Lead status updated successfully"));
    }

    /**
     * Converts a qualified lead into a project.
     *
     * <p>The lead must be assigned to a user and must have a
     * {@code QUALIFIED} status before it can be converted.</p>
     *
     * @param id      the unique identifier of the lead to convert
     * @param request the project details used during conversion
     * @return a response containing the newly created project
     */
    @PostMapping("/{id}/convert-to-project")
    public ResponseEntity<ApiResponse<ProjectResponse>> leadToProject(@PathVariable String id, @Valid @RequestBody LeadToProjectRequest request) {

        return ResponseEntity.ok(ApiResponse.ok(leadService.leadToProject(id, request), "Lead converted to project successfully"));
    }
}