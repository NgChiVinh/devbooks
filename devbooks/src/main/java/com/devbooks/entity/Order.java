package com.devbooks.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(length = 100)
    private String city;

    @Column(nullable = false, length = 50)
    private String status;

    // ❌ Dòng @ManyToOne bị thừa đã được XÓA BỎ khỏi đây

    @Column(length = 50) // Đây là cột của paymentMethod
    private String paymentMethod;

    // Đây mới là @ManyToOne của User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // Hàm toString() an toàn (đã sửa ở bước trước)
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}