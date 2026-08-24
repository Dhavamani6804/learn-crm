package com.dhava.crmdemo.controller;

import com.dhava.crmdemo.api.ApiResponse;
import com.dhava.crmdemo.dto.request.ProjectRequest;
import com.dhava.crmdemo.dto.request.ProjectStatusRequest;
import com.dhava.crmdemo.dto.response.ProjectResponse;
import com.dhava.crmdemo.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response, "Project created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getAllProjects(), "Projects fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.getProjectById(id), "Project fetched successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(@PathVariable String id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.updateProject(id, request), "Project updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);

        return ResponseEntity.ok(ApiResponse.noContent("Project deleted successfully"));
    }

    @PatchMapping("/{id}/assign/{userId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> assignProject(@PathVariable String id, @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.assignUserToProject(id, userId), "Project assigned to user successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProjectStatus(@PathVariable String id, @Valid @RequestBody ProjectStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(projectService.updateProjectStatus(id, request), "Project status updated successfully"));
    }
}