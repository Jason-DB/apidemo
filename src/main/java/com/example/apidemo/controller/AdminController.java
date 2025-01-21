package com.example.apidemo.controller;

import com.example.apidemo.model.ApiResponse;
import com.example.apidemo.model.Permission;
import com.example.apidemo.model.Role;
import com.example.apidemo.model.User;
import com.example.apidemo.repository.PermissionRepository;
import com.example.apidemo.repository.RoleRepository;
import com.example.apidemo.repository.UserRepository;
import com.example.apidemo.security.Permissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('" + Permissions.VIEW_USERS + "')")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ApiResponse<>(20000, users);
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_USER + "')")
    public ApiResponse<User> createUser(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            return new ApiResponse<>(20000, savedUser);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(50012, null);
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_USER + "')")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            user.setUsername(updatedUser.getUsername());
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            user.setEnabled(updatedUser.isEnabled());
            User savedUser = userRepository.save(user);
            return new ApiResponse<>(20000, savedUser);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(50012, null);
        }
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_USER + "')")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return new ApiResponse<>(20000, null);
    }

    @PutMapping("/users/{id}/reset-password")
    @PreAuthorize("hasAuthority('" + Permissions.RESET_PASSWORD + "')")
    public ApiResponse<String> resetPassword(@PathVariable Long id, @RequestBody String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new ApiResponse<>(20000, null);
    }

    @PutMapping("/users/{id}/assign-role")
    @PreAuthorize("hasAuthority('" + Permissions.ASSIGN_ROLE + "')")
    public ApiResponse<String> assignRoleToUser(@PathVariable Long id, @RequestBody Long roleId) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
        return new ApiResponse<>(20000, null);
    }

    @PutMapping("/roles/{id}/assign-permission")
    @PreAuthorize("hasAuthority('" + Permissions.ASSIGN_PERMISSION + "')")
    public ApiResponse<String> assignPermissionToRole(@PathVariable Long id, @RequestBody Long permissionId) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new RuntimeException("Permission not found"));
        role.getPermissions().add(permission);
        roleRepository.save(role);
        return new ApiResponse<>(20000, null);
    }

    // 角色管理
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_ROLES + "')")
    public ApiResponse<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return new ApiResponse<>(20000, roles);
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_ROLES + "')")
    public ApiResponse<Role> createRole(@RequestBody Role role) {
        try {
            Role savedRole = roleRepository.save(role);
            return new ApiResponse<>(20000, savedRole);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(50012, null);
        }
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_ROLES + "')")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        try {
            Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
            role.setName(updatedRole.getName());
            Role savedRole = roleRepository.save(role);
            return new ApiResponse<>(20000, savedRole);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(50012, null);
        }
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_ROLES + "')")
    public ApiResponse<String> deleteRole(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return new ApiResponse<>(20000, null);
    }

    // 权限管理
    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_PERMISSIONS + "')")
    public ApiResponse<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return new ApiResponse<>(20000, permissions);
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_PERMISSIONS + "')")
    public ApiResponse<Permission> createPermission(@RequestBody Permission permission) {
        try {
            Permission savedPermission = permissionRepository.save(permission);
            return new ApiResponse<>(20000, savedPermission);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(50012, null);
        }
    }

    @PutMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_PERMISSIONS + "')")
    public ApiResponse<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission updatedPermission) {
        try {
            Permission permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission not found"));
            permission.setName(updatedPermission.getName());
            permission.setDescription(updatedPermission.getDescription());
            Permission savedPermission = permissionRepository.save(permission);
            return new ApiResponse<>(20000, savedPermission);
        } catch (DataIntegrityViolationException e) {
            return new ApiResponse<>(50012, null);
        }
    }

    @DeleteMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_PERMISSIONS + "')")
    public ApiResponse<String> deletePermission(@PathVariable Long id) {
        permissionRepository.deleteById(id);
        return new ApiResponse<>(20000, null);
    }
}