package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.request.CreateUserRequest;
import com.dhava.crmdemo.dto.request.UpdateOwnProfileRequest;
import com.dhava.crmdemo.dto.request.UpdateUserRequest;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.User;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.enums.Role;
import com.dhava.crmdemo.exception.AuthorizationException;
import com.dhava.crmdemo.exception.UserAlreadyExistException;
import com.dhava.crmdemo.exception.UserNotFoundException;
import com.dhava.crmdemo.mapper.UserMapper;
import com.dhava.crmdemo.repository.UserRepository;
import com.dhava.crmdemo.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.dhava.crmdemo.constants.Constants.PASSWORD_CHANGED;
import static com.dhava.crmdemo.constants.Constants.USER_NOT_FOUND_WITH_ID;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    @Transactional
    public UserResponse addUser(CreateUserRequest request) {

        User actor = securityUtils.getCurrentUser();
        Role requestedRole = validateCreationRole(request.getRole(), actor);
        validateUniqueFields(request.getEmail(), request.getPhone());

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(requestedRole);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        activityLogService.logActivity(EntityType.USER, String.valueOf(savedUser.getId()), ActivityType.CREATE, "User created", null, "User " + savedUser.getName() + " created with role " + savedUser.getRole());

        return userMapper.toUserResponse(savedUser);
    }

    private Role validateCreationRole(Role requestedRole, User actor) {

        if (actor.getRole() == Role.SUPER_ADMIN) {

            if (requestedRole == Role.SUPER_ADMIN) {
                throw new AuthorizationException("SUPER_ADMIN cannot be created through this API");
            }
            return requestedRole;
        }

        if (actor.getRole() == Role.ADMIN) {

            if (requestedRole != Role.USER) {
                throw new AuthorizationException("ADMIN can create only USER accounts");
            }
            return Role.USER;
        }
        throw new AuthorizationException("USER does not have permission to create users");
    }

    private void validateUniqueFields(String email, String phone) {

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistException("Email already exists");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new UserAlreadyExistException("Phone already exists");
        }
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_WITH_ID + id));
        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User actor = securityUtils.getCurrentUser();
        User target = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_WITH_ID + id));

        if (actor.getRole() == Role.USER) {
            throw new AuthorizationException("USER does not have permission to update users");
        }

        if (actor.getRole() == Role.ADMIN && actor.getId().equals(target.getId())) {
            throw new AuthorizationException("ADMIN cannot update his own account");
        }

        if (actor.getRole() == Role.ADMIN && target.getRole() != Role.USER) {
            throw new AuthorizationException("ADMIN can update only USER accounts");
        }

        if (actor.getRole() == Role.SUPER_ADMIN && actor.getId().equals(target.getId())) {
            updateSuperAdminProfile(target, request);
            User savedUser = userRepository.save(target);
            return userMapper.toUserResponse(savedUser);
        }

        if (actor.getRole() == Role.SUPER_ADMIN) {
            updateManagedUser(target, request);
            User savedUser = userRepository.save(target);
            return userMapper.toUserResponse(savedUser);
        }

        if (actor.getRole() == Role.ADMIN) {
            updateManagedUser(target, request);
            User savedUser = userRepository.save(target);
            return userMapper.toUserResponse(savedUser);
        }

        throw new AuthorizationException("You do not have permission to update this user");
    }

    private void updateSuperAdminProfile(User user, UpdateUserRequest request) {

        if (!Objects.equals(user.getName(), request.getName())) {
            activityLogService.logActivity(EntityType.USER, String.valueOf(user.getId()), ActivityType.UPDATE, "Super admin name updated", user.getName(), request.getName());
            user.setName(request.getName());
        }

        if (!Objects.equals(user.getPhone(), request.getPhone())) {

            if (userRepository.existsByPhoneAndIdNot(request.getPhone(), user.getId())) {
                throw new UserAlreadyExistException("Phone already exists");
            }

            activityLogService.logActivity(EntityType.USER, String.valueOf(user.getId()), ActivityType.UPDATE, "Super admin phone updated", user.getPhone(), request.getPhone());
            user.setPhone(request.getPhone());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {

            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            activityLogService.logActivity(EntityType.USER, String.valueOf(user.getId()), ActivityType.UPDATE, "Super admin password updated", null, PASSWORD_CHANGED);
        }

    }

    private void updateManagedUser(User user, UpdateUserRequest request) {

        if (!Objects.equals(user.getEmail(), request.getEmail())) {

            if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
                throw new UserAlreadyExistException("Email already exists");
            }

            activityLogService.logActivity(EntityType.USER, String.valueOf(user.getId()), ActivityType.UPDATE, "User email updated", user.getEmail(), request.getEmail());
            user.setEmail(request.getEmail());
        }

        if (!Objects.equals(user.getPhone(), request.getPhone())) {

            if (userRepository.existsByPhoneAndIdNot(request.getPhone(), user.getId())) {
                throw new UserAlreadyExistException("Phone already exists");
            }

            activityLogService.logActivity(EntityType.USER, String.valueOf(user.getId()), ActivityType.UPDATE, "User phone updated", user.getPhone(), request.getPhone());
            user.setPhone(request.getPhone());
        }

        if (!Objects.equals(user.getName(), request.getName())) {
            activityLogService.logActivity(EntityType.USER, String.valueOf(user.getId()), ActivityType.UPDATE, "User name updated", user.getName(), request.getName());
            user.setName(request.getName());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            activityLogService.logActivity(EntityType.USER, String.valueOf(user.getId()), ActivityType.UPDATE, "User password updated", null, PASSWORD_CHANGED);
        }

        if (request.getIsActive() != null && !Objects.equals(user.getIsActive(), request.getIsActive())) {
            activityLogService.logActivity(EntityType.USER, String.valueOf(user.getId()), ActivityType.UPDATE, "User active status updated", String.valueOf(user.getIsActive()), String.valueOf(request.getIsActive()));
            user.setIsActive(request.getIsActive());
        }
    }

    @Transactional
    public UserResponse updateOwnProfile(UpdateOwnProfileRequest request) {

        User actor = securityUtils.getCurrentUser();

        if (actor.getRole() != Role.SUPER_ADMIN) {
            throw new AuthorizationException("Only SUPER_ADMIN can update their own profile");
        }

        if (!Objects.equals(actor.getName(), request.getName())) {
            activityLogService.logActivity(EntityType.USER, String.valueOf(actor.getId()), ActivityType.UPDATE, "Super admin name updated", actor.getName(), request.getName());
            actor.setName(request.getName());
        }

        if (!Objects.equals(actor.getPhone(), request.getPhone())) {

            if (userRepository.existsByPhoneAndIdNot(request.getPhone(), actor.getId())) {
                throw new UserAlreadyExistException("Phone already exists");
            }
            activityLogService.logActivity(EntityType.USER, String.valueOf(actor.getId()), ActivityType.UPDATE, "Super admin phone updated", actor.getPhone(), request.getPhone());
            actor.setPhone(request.getPhone());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            actor.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            activityLogService.logActivity(EntityType.USER, String.valueOf(actor.getId()), ActivityType.UPDATE, "Super admin password updated", null, PASSWORD_CHANGED);
        }
        User savedUser = userRepository.save(actor);
        return userMapper.toUserResponse(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        User actor = securityUtils.getCurrentUser();
        User target = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_WITH_ID + id));

        if (target.getRole() == Role.SUPER_ADMIN) {
            throw new AuthorizationException("SUPER_ADMIN account cannot be deleted");
        }

        if (actor.getRole() == Role.USER) {
            throw new AuthorizationException("USER does not have permission to delete users");
        }

        if (actor.getRole() == Role.ADMIN && target.getRole() != Role.USER) {
            throw new AuthorizationException("ADMIN can delete only USER accounts");
        }

        String oldValue = "name=" + target.getName() + ", email=" + target.getEmail() + ", phone=" + target.getPhone() + ", role=" + target.getRole();
        userRepository.delete(target);
        activityLogService.logActivity(EntityType.USER, String.valueOf(id), ActivityType.DELETE, "User deleted", oldValue, null);
    }
}

