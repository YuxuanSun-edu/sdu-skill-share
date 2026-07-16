package com.shanwei.user.service;

import com.shanwei.user.dto.LoginRequest;
import com.shanwei.user.dto.LoginResponse;
import com.shanwei.user.dto.RegisterRequest;
import com.shanwei.user.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    User currentUser(Long userId);

    Map<String, Object> profile(Long userId);

    List<User> adminUsers(Long adminId);

    User adminCreateUser(Long adminId, RegisterRequest request);

    User adminUpdateUser(Long adminId, Long id, RegisterRequest request);

    void adminDeleteUser(Long adminId, Long id);
}
