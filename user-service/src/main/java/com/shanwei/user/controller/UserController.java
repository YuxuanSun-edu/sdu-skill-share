package com.shanwei.user.controller;

import com.shanwei.common.ApiResponse;
import com.shanwei.user.dto.LoginRequest;
import com.shanwei.user.dto.LoginResponse;
import com.shanwei.user.dto.RegisterRequest;
import com.shanwei.user.entity.User;
import com.shanwei.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest request) {
        return ApiResponse.success(userService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(userService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<User> me(HttpServletRequest request) {
        return ApiResponse.success(userService.currentUser((Long) request.getAttribute("userId")));
    }

    @GetMapping("/public/{id}")
    public ApiResponse<User> publicUser(@PathVariable Long id) {
        return ApiResponse.success(userService.currentUser(id));
    }

    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> profile(HttpServletRequest request) {
        return ApiResponse.success(userService.profile((Long) request.getAttribute("userId")));
    }

    @GetMapping("/admin/users")
    public ApiResponse<List<User>> adminUsers(HttpServletRequest request) {
        return ApiResponse.success(userService.adminUsers((Long) request.getAttribute("userId")));
    }

    @PostMapping("/admin/users")
    public ApiResponse<User> adminCreateUser(HttpServletRequest request, @RequestBody RegisterRequest body) {
        return ApiResponse.success(userService.adminCreateUser((Long) request.getAttribute("userId"), body));
    }

    @PutMapping("/admin/users/{id}")
    public ApiResponse<User> adminUpdateUser(HttpServletRequest request, @PathVariable Long id, @RequestBody RegisterRequest body) {
        return ApiResponse.success(userService.adminUpdateUser((Long) request.getAttribute("userId"), id, body));
    }

    @DeleteMapping("/admin/users/{id}")
    public ApiResponse<Void> adminDeleteUser(HttpServletRequest request, @PathVariable Long id) {
        userService.adminDeleteUser((Long) request.getAttribute("userId"), id);
        return ApiResponse.success();
    }
}
