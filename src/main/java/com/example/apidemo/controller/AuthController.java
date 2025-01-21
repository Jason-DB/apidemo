package com.example.apidemo.controller;

import com.example.apidemo.model.ApiResponse;
import com.example.apidemo.model.AuthenticationRequest;
import com.example.apidemo.model.AuthenticationResponse;
import com.example.apidemo.model.User;
import com.example.apidemo.service.CustomUserDetailsService;
import com.example.apidemo.util.JwtUtil;
import com.example.apidemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/authenticate")
    public ApiResponse<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails.getUsername());

        // 从数据库中获取用户信息
        User user = userRepository.findByUsername(authenticationRequest.getUsername());
        if (user == null) {
            return new ApiResponse<>(50008, null);
        }

        AuthenticationResponse authResponse = new AuthenticationResponse(token);
        return new ApiResponse<>(20000, authResponse);
    }
    
    @PostMapping("/logout")
    public ApiResponse<String> logout() {
    // 这里可以添加实际的登出逻辑，例如使 JWT 失效等
    return new ApiResponse<>(20000, "success");
}
}