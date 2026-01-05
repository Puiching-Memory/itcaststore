package cn.itcast.itcaststore.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal money;

    @Column(length = 255)
    private String receiverAddress;

    @Column(length = 20)
    private String receiverName;

    @Column(length = 20)
    private String receiverPhone;

    @Column(nullable = false)
    private Integer paystate = 0; // 0:未支付 1:已支付

    @Column(nullable = false, updatable = false)
    private LocalDateTime ordertime = LocalDateTime.now();

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> orderItems;
}
