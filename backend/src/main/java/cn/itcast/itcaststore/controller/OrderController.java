package cn.itcast.itcaststore.controller;

import cn.itcast.itcaststore.domain.Order;
import cn.itcast.itcaststore.dto.CreateOrderRequest;
import cn.itcast.itcaststore.domain.User;
import cn.itcast.itcaststore.repository.OrderRepository;
import cn.itcast.itcaststore.repository.UserRepository;
import cn.itcast.itcaststore.security.JwtTokenProvider;
import cn.itcast.itcaststore.service.OrderService;
import cn.itcast.itcaststore.util.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final JwtTokenProvider tokenProvider;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, JwtTokenProvider tokenProvider, OrderService orderService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.tokenProvider = tokenProvider;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseResult<Page<Order>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未授权");
        }
        String token = authHeader.substring(7);
        Long userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("无效的Token");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return ResponseResult.success(orders);
    }

    @GetMapping("/list")
    public ResponseResult<List<Order>> getOrderList(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未授权");
        }
        String token = authHeader.substring(7);
        Long userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("无效的Token");
        }
        
        List<Order> orders = orderRepository.findByUserIdOrderByOrdertimeDesc(userId);
        return ResponseResult.success(orders);
    }

    @PostMapping
    public ResponseResult<Order> createOrder(@RequestBody CreateOrderRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未授权");
        }
        String token = authHeader.substring(7);
        Long userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("无效的Token");
        }
        Order savedOrder = orderService.createOrder(request, userId);
        return ResponseResult.success(savedOrder);
    }

    /**
     * 管理员查看所有订单
     */
    @GetMapping("/admin/all")
    public ResponseResult<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未授权");
        }
        String token = authHeader.substring(7);
        Long userId = tokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("无效的Token");
        }

        // 校验是否为管理员
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!"超级用户".equals(user.getRole()) && !"管理员".equals(user.getRole())) {
            throw new RuntimeException("权限不足，仅管理员可访问");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAll(pageable);
        return ResponseResult.success(orders);
    }
}
