package cn.itcast.itcaststore.service;

import cn.itcast.itcaststore.domain.Order;
import cn.itcast.itcaststore.domain.OrderItem;
import cn.itcast.itcaststore.domain.OrderItemId;
import cn.itcast.itcaststore.domain.Product;
import cn.itcast.itcaststore.dto.CreateOrderRequest;
import cn.itcast.itcaststore.repository.OrderRepository;
import cn.itcast.itcaststore.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * 创建订单：聚合重复商品、校验库存、扣减库存、持久化订单和订单项。
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request, Long userId) {
        if (request == null) {
            throw new RuntimeException("请求不能为空");
        }
        if (userId == null) {
            throw new RuntimeException("未授权");
        }
        if (request.getOrderItems() == null || request.getOrderItems().isEmpty()) {
            throw new RuntimeException("订单项不能为空");
        }

        Map<String, Integer> aggregatedItems = new LinkedHashMap<>();
        request.getOrderItems().forEach(item -> {
            if (item == null || item.getProductId() == null || item.getProductId().isBlank()) {
                throw new RuntimeException("存在无效的商品编号");
            }
            if (item.getBuynum() == null || item.getBuynum() <= 0) {
                throw new RuntimeException("购买数量必须大于0");
            }
            aggregatedItems.merge(item.getProductId(), item.getBuynum(), Integer::sum);
        });

        if (aggregatedItems.isEmpty()) {
            throw new RuntimeException("订单项不能为空");
        }

        List<Product> products = productRepository.findAllById(aggregatedItems.keySet());
        if (products.size() != aggregatedItems.size()) {
            Set<String> foundIds = products.stream().map(Product::getId).collect(Collectors.toSet());
            List<String> missing = aggregatedItems.keySet().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new RuntimeException("商品不存在或已下架: " + String.join(",", missing));
        }

        BigDecimal totalMoney = BigDecimal.ZERO;
        for (Product product : products) {
            int need = aggregatedItems.get(product.getId());
            if (product.getPnum() < need) {
                throw new RuntimeException("库存不足: " + product.getName());
            }
            totalMoney = totalMoney.add(product.getPrice().multiply(BigDecimal.valueOf(need)));
        }

        String orderId = UUID.randomUUID().toString();

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setPaystate(0);
        order.setMoney(totalMoney);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());

        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : products) {
            int need = aggregatedItems.get(product.getId());
            product.setPnum(product.getPnum() - need);

            OrderItemId itemId = new OrderItemId(orderId, product.getId());

            OrderItem orderItem = new OrderItem();
            orderItem.setId(itemId);
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setBuynum(need);

            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        productRepository.saveAll(products);

        return savedOrder;
    }
}
