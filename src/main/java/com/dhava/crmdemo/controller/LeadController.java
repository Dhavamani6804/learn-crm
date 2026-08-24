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
        return  ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response,"Lead created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getAllLeads() {
        return ResponseEntity.ok(ApiResponse.ok(leadService.getAllLeads(),"Fetched all leads"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadById(@PathVariable long id) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.getLeadById(id),"Fetched a lead"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLead(@PathVariable long id,@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.updateLead(id,request),"Lead details updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable long id) {
        leadService.deleteLeadById(id);
        return ResponseEntity.ok(ApiResponse.noContent("Lead deleted successfully"));
    }

    @PatchMapping("/{id}/assign/{userId}")
    public ResponseEntity<ApiResponse<LeadResponse>> assignUserToLead(@PathVariable long id, @PathVariable long userId) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.assignUserToLead(id,userId),"Lead assigned to User successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LeadResponse>> updateLeadStatus(@PathVariable long id, @RequestBody LeadStatusRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.updateLeadStatus(id,req),"Lead status updated successfully"));
    }

    @PostMapping("/{id}/convert-to-project")
    public ResponseEntity<ApiResponse<ProjectResponse>> leadToProject(@PathVariable long id,@Valid @RequestBody LeadToProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.leadToProject(id,request),"Lead converted to project successfully"));
    }

}
