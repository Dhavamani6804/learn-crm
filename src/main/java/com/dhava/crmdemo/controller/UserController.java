package com.dhava.crmdemo.controller;

import com.dhava.crmdemo.api.ApiResponse;
import com.dhava.crmdemo.dto.request.CreateUserRequest;
import com.dhava.crmdemo.dto.request.UpdateOwnProfileRequest;
import com.dhava.crmdemo.dto.request.UpdateUserRequest;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> addUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.addUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response, "User created successfully"));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers(), "Users fetched successfully"));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id), "User fetched successfully"));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateOwnProfile(@Valid @RequestBody UpdateOwnProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateOwnProfile(request), "Profile updated successfully"));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(userService.updateUser(id, request), "User updated successfully"));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.noContent("User deleted successfully"));
    }
}
