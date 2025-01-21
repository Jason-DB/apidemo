package com.example.apidemo.controller;

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
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('" + Permissions.CREATE_USER + "')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return ResponseEntity.ok(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_USER + "')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            user.setUsername(updatedUser.getUsername());
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            user.setEnabled(updatedUser.isEnabled());
            return ResponseEntity.ok(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_USER + "')")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted successfully";
    }

    @PutMapping("/users/{id}/reset-password")
    @PreAuthorize("hasAuthority('" + Permissions.RESET_PASSWORD + "')")
    public String resetPassword(@PathVariable Long id, @RequestBody String newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password reset successfully";
    }

    @PutMapping("/users/{id}/assign-role")
    @PreAuthorize("hasAuthority('" + Permissions.ASSIGN_ROLE + "')")
    public String assignRoleToUser(@PathVariable Long id, @RequestBody Long roleId) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
        return "Role assigned successfully";
    }

    @PutMapping("/roles/{id}/assign-permission")
    @PreAuthorize("hasAuthority('" + Permissions.ASSIGN_PERMISSION + "')")
    public String assignPermissionToRole(@PathVariable Long id, @RequestBody Long permissionId) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new RuntimeException("Permission not found"));
        role.getPermissions().add(permission);
        roleRepository.save(role);
        return "Permission assigned successfully";
    }

    // 角色管理
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_ROLES + "')")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_ROLES + "')")
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        try {
            return ResponseEntity.ok(roleRepository.save(role));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Role name already exists");
        }
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_ROLES + "')")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        try {
            Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
            role.setName(updatedRole.getName());
            return ResponseEntity.ok(roleRepository.save(role));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Role name already exists");
        }
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_ROLES + "')")
    public String deleteRole(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return "Role deleted successfully";
    }

    // 权限管理
    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_PERMISSIONS + "')")
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_PERMISSIONS + "')")
    public ResponseEntity<?> createPermission(@RequestBody Permission permission) {
        try {
            return ResponseEntity.ok(permissionRepository.save(permission));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Permission name already exists");
        }
    }

    @PutMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_PERMISSIONS + "')")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @RequestBody Permission updatedPermission) {
        try {
            Permission permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission not found"));
            permission.setName(updatedPermission.getName());
            permission.setDescription(updatedPermission.getDescription());
            return ResponseEntity.ok(permissionRepository.save(permission));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Permission name already exists");
        }
    }

    @DeleteMapping("/permissions/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MANAGE_PERMISSIONS + "')")
    public String deletePermission(@PathVariable Long id) {
        permissionRepository.deleteById(id);
        return "Permission deleted successfully";
    }
}