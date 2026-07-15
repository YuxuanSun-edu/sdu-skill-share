package com.shanwei.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanwei.common.BizException;
import com.shanwei.common.JwtUtil;
import com.shanwei.user.dto.LoginRequest;
import com.shanwei.user.dto.LoginResponse;
import com.shanwei.user.dto.RegisterRequest;
import com.shanwei.user.entity.User;
import com.shanwei.user.mapper.UserMapper;
import com.shanwei.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User register(RegisterRequest request) {
        if (!StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())
                || !StringUtils.hasText(request.getNickname())
                || !StringUtils.hasText(request.getEmail())
                || !StringUtils.hasText(request.getPhone())) {
            throw new BizException("请完整填写注册信息");
        }
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BizException("账号已存在，请更换账号");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar("https://api.dicebear.com/8.x/initials/svg?seed=" + request.getUsername());
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .eq(User::getPassword, request.getPassword()));
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        String token = JwtUtil.createToken(user.getId(), user.getUsername());
        redisTemplate.opsForValue().set("login:" + user.getId(), token, Duration.ofHours(12));
        user.setPassword(null);
        return new LoginResponse(token, user);
    }

    @Override
    public User currentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public Map<String, Object> profile(Long userId) {
        User user = currentUser(userId);
        return Map.of(
                "user", user,
                "mySkills", List.of("Python数据分析", "PPT美化"),
                "myRequests", List.of("毕业海报设计"),
                "myOrders", List.of("待确认预约 1 个"),
                "myComments", List.of("教学认真，非常专业")
        );
    }

    @Override
    public List<User> adminUsers(Long adminId) {
        requireAdmin(adminId);
        List<User> users = userMapper.selectList(new LambdaQueryWrapper<User>().orderByDesc(User::getCreateTime));
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public User adminCreateUser(Long adminId, RegisterRequest request) {
        requireAdmin(adminId);
        return register(request);
    }

    @Override
    public User adminUpdateUser(Long adminId, Long id, RegisterRequest request) {
        requireAdmin(adminId);
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (StringUtils.hasText(request.getUsername()) && !request.getUsername().equals(user.getUsername())) {
            Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
            if (count > 0) {
                throw new BizException("账号已存在，请更换账号");
            }
            user.setUsername(request.getUsername());
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(request.getPassword());
        }
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        userMapper.updateById(user);
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public void adminDeleteUser(Long adminId, Long id) {
        requireAdmin(adminId);
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if ("admin".equals(user.getUsername())) {
            throw new BizException("管理员账号不能删除");
        }
        deleteUserBusinessData(id);
        userMapper.deleteById(id);
        redisTemplate.delete("login:" + id);
    }

    private void deleteUserBusinessData(Long userId) {
        jdbcTemplate.update("DELETE FROM message WHERE to_user = ?", userId);
        jdbcTemplate.update("""
                DELETE FROM comment
                WHERE from_user = ?
                   OR to_user = ?
                   OR order_id IN (
                       SELECT order_id FROM service_order
                       WHERE buyer_id = ?
                          OR seller_id = ?
                          OR skill_id IN (SELECT skill_id FROM skill WHERE user_id = ?)
                   )
                """, userId, userId, userId, userId, userId);
        jdbcTemplate.update("""
                DELETE FROM service_order
                WHERE buyer_id = ?
                   OR seller_id = ?
                   OR skill_id IN (SELECT skill_id FROM skill WHERE user_id = ?)
                """, userId, userId, userId);
        jdbcTemplate.update("DELETE FROM service_request WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM skill WHERE user_id = ?", userId);
    }

    private void requireAdmin(Long adminId) {
        User admin = userMapper.selectById(adminId);
        if (admin == null || !"admin".equals(admin.getUsername())) {
            throw new BizException("只有管理员可以操作");
        }
    }
}
