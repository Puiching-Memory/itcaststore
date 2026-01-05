package cn.itcast.itcaststore.controller;

import cn.itcast.itcaststore.domain.User;
import cn.itcast.itcaststore.dto.AdminUpdateUserRoleRequest;
import cn.itcast.itcaststore.dto.AdminUpdateUserStateRequest;
import cn.itcast.itcaststore.dto.UpdateUserRequest;
import cn.itcast.itcaststore.repository.UserRepository;
import cn.itcast.itcaststore.security.JwtTokenProvider;
import cn.itcast.itcaststore.util.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Set<String> ADMIN_ROLES = Set.of("超级用户", "管理员");
    private static final Set<String> ALLOWED_ROLES = Set.of("普通用户", "管理员", "超级用户");

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public UserController(UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/me")
    public ResponseResult<User> getCurrentUser(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return ResponseResult.success(user);
    }

    @PutMapping("/me")
    public ResponseResult<User> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request,
            HttpServletRequest httpRequest) {
        User user = getUserFromRequest(httpRequest);
        
        // 更新允许修改的字段
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getTelephone() != null) {
            user.setTelephone(request.getTelephone());
        }
        if (request.getIntroduce() != null) {
            user.setIntroduce(request.getIntroduce());
        }
        
        user = userRepository.save(user);
        return ResponseResult.success(user);
    }

    @GetMapping("/admin")
    public ResponseResult<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {

        User currentUser = getUserFromRequest(request);
        ensureAdmin(currentUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "registTime"));
        Page<User> users = StringUtils.hasText(keyword)
                ? userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable)
                : userRepository.findAll(pageable);

        return ResponseResult.success(users);
    }

    @PutMapping("/admin/{id}/role")
    public ResponseResult<User> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRoleRequest request,
            HttpServletRequest httpRequest) {

        User currentUser = getUserFromRequest(httpRequest);
        ensureAdmin(currentUser);

        if (!ALLOWED_ROLES.contains(request.getRole())) {
            throw new RuntimeException("角色无效，只能切换为普通用户、管理员或超级用户");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setRole(request.getRole());

        user = userRepository.save(user);
        return ResponseResult.success("角色更新成功", user);
    }

    @PutMapping("/admin/{id}/state")
    public ResponseResult<User> updateUserState(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserStateRequest request,
            HttpServletRequest httpRequest) {

        User currentUser = getUserFromRequest(httpRequest);
        ensureAdmin(currentUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setState(request.getState());
        user = userRepository.save(user);
        return ResponseResult.success("用户状态已更新", user);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未授权");
        }
        String token = authHeader.substring(7);
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException("Token无效或已过期");
        }
        Long userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("无效的Token");
        }
        return userId;
    }

    private User getUserFromRequest(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    private void ensureAdmin(User user) {
        if (!ADMIN_ROLES.contains(user.getRole())) {
            throw new RuntimeException("权限不足，仅管理员可访问");
        }
    }
}

