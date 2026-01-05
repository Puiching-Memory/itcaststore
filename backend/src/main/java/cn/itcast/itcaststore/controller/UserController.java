package cn.itcast.itcaststore.controller;

import cn.itcast.itcaststore.domain.User;
import cn.itcast.itcaststore.dto.UpdateUserRequest;
import cn.itcast.itcaststore.repository.UserRepository;
import cn.itcast.itcaststore.security.JwtTokenProvider;
import cn.itcast.itcaststore.util.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public UserController(UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/me")
    public ResponseResult<User> getCurrentUser(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return ResponseResult.success(user);
    }

    @PutMapping("/me")
    public ResponseResult<User> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request,
            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
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
}

