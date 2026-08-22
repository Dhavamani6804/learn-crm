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

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse addUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())||userRepository.existsByPhone(request.getPhone())) {
            throw new UserAlreadyExistException("Email or Phone already exists");
        }

        User newUser = new User();

        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setIsActive(true);

        User savedUser = userRepository.save(newUser);

        activityLogService.logActivity(
                EntityType.USER,
                savedUser.getId(),
                ActivityType.CREATE,
                "User Created",
                null,
                null,
                "User "+savedUser.getName()+" has been created"
        );

        return userMapper.toUserResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new UserAlreadyExistException("Email already exists");
        }

        if (userRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
            throw new UserAlreadyExistException("Phone already exists");
        }

        String oldValue = "name="+user.getName()+", email="+user.getEmail()+", phone="+user.getPhone();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        User savedUser = userRepository.save(user);

        String newValue = "name="+user.getName()+", email="+user.getEmail()+", phone="+user.getPhone();

        activityLogService.logActivity(
                EntityType.USER,
                savedUser.getId(),
                ActivityType.UPDATE,
                "User details updated",
                null,
                oldValue,
                newValue
        );

        return userMapper.toUserResponse(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with the id: " + id));
        String oldValue = "name="+user.getName()+", email="+user.getEmail()+", phone="+user.getPhone();
        userRepository.delete(user);

        activityLogService.logActivity(
                EntityType.USER,
                id,
                ActivityType.DELETE,
                "User deleted",
                null,
                oldValue,
                null
        );
    }

}
