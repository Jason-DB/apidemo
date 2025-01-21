package com.example.apidemo.controller;

import com.example.apidemo.model.ApiResponse;
import com.example.apidemo.model.User;
import com.example.apidemo.model.Role;
import com.example.apidemo.model.Permission;
import com.example.apidemo.repository.UserRepository;
import com.example.apidemo.security.Permissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('" + Permissions.VIEW_USERS + "')")
    public ApiResponse<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
    
        // 获取用户角色名称
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", user.getUsername());
        responseData.put("roles", roles);
        responseData.put("avatar", user.getAvatar());
        responseData.put("introduction", user.getIntroduction());
    
        return new ApiResponse<>(20000, responseData);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_USER + "')")
    public ApiResponse<Map<String, User>> updateCurrentUser(@RequestBody User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        user.setUsername(updatedUser.getUsername());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        user.setEnabled(updatedUser.isEnabled());
        userRepository.save(user);
        Map<String, User> responseData = new HashMap<>();
        responseData.put("user", user);
        return new ApiResponse<>(20000,  responseData);
    }
}