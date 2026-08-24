package com.dhava.crmdemo.controller;

import com.dhava.crmdemo.api.ApiResponse;
import com.dhava.crmdemo.dto.request.LeadRequest;
import com.dhava.crmdemo.dto.request.LeadStatusRequest;
import com.dhava.crmdemo.dto.request.LeadToProjectRequest;
import com.dhava.crmdemo.dto.response.LeadResponse;
import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.service.LeadService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(@Valid @RequestBody LeadRequest request) {
        LeadResponse response = leadService.createLead(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response, "Lead created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getAllLeads() {
        return ResponseEntity.ok(ApiResponse.ok(leadService.getAllLeads(), "Leads fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.getLeadById(id), "Lead fetched successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(@PathVariable String id, @Valid @RequestBody LeadRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.updateLead(id, request), "Lead updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable String id) {
        leadService.deleteLeadById(id);

        return ResponseEntity.ok(ApiResponse.noContent("Lead deleted successfully"));
    }

    @PatchMapping("/{id}/assign/{userId}")
    public ResponseEntity<ApiResponse<LeadResponse>> assignUserToLead(@PathVariable String id, @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.assignUserToLead(id, userId), "Lead assigned to user successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLeadStatus(@PathVariable String id, @Valid @RequestBody LeadStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.updateLeadStatus(id, request), "Lead status updated successfully"));
    }

    @PostMapping("/{id}/convert-to-project")
    public ResponseEntity<ApiResponse<ProjectResponse>> leadToProject(@PathVariable String id, @Valid @RequestBody LeadToProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.leadToProject(id, request), "Lead converted to project successfully"));
    }
}