package com.dhava.crmdemo.service;

import com.dhava.crmdemo.dto.request.UserRequest;
import com.dhava.crmdemo.dto.response.UserResponse;
import com.dhava.crmdemo.entity.User;
import com.dhava.crmdemo.enums.ActivityType;
import com.dhava.crmdemo.enums.EntityType;
import com.dhava.crmdemo.exception.UserAlreadyExistException;
import com.dhava.crmdemo.exception.UserNotFoundException;
import com.dhava.crmdemo.mapper.UserMapper;
import com.dhava.crmdemo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse addUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByPhone(request.getPhone())) {

            throw new UserAlreadyExistException("Email or Phone already exists");
        }

        User newUser = new User();

        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setIsActive(true);

        User savedUser = userRepository.save(newUser);

        activityLogService.logActivity(EntityType.USER, String.valueOf(savedUser.getId()), ActivityType.CREATE, "User created", null, null, "User " + savedUser.getName() + " has been created");

        return userMapper.toUserResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }


    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!Objects.equals(user.getEmail(), request.getEmail()) && userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {

            throw new UserAlreadyExistException("Email already exists");
        }

        if (!Objects.equals(user.getPhone(), request.getPhone()) && userRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {

            throw new UserAlreadyExistException("Phone already exists");
        }

        if (!Objects.equals(user.getName(), request.getName())) {

            activityLogService.logActivity(EntityType.USER, String.valueOf(id), ActivityType.UPDATE, "User name updated", null, user.getName(), request.getName());

            user.setName(request.getName());
        }

        if (!Objects.equals(user.getEmail(), request.getEmail())) {

            activityLogService.logActivity(EntityType.USER, String.valueOf(id), ActivityType.UPDATE, "User email updated", null, user.getEmail(), request.getEmail());

            user.setEmail(request.getEmail());
        }

        if (!Objects.equals(user.getPhone(), request.getPhone())) {

            activityLogService.logActivity(EntityType.USER, String.valueOf(id), ActivityType.UPDATE, "User phone updated", null, user.getPhone(), request.getPhone());

            user.setPhone(request.getPhone());
        }


        User savedUser = userRepository.save(user);

        return userMapper.toUserResponse(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with the id: " + id));

        String oldValue = "name=" + user.getName() + ", email=" + user.getEmail() + ", phone=" + user.getPhone();

        userRepository.delete(user);

        activityLogService.logActivity(EntityType.USER, String.valueOf(id), ActivityType.DELETE, "User deleted", null, oldValue, null);
    }
}