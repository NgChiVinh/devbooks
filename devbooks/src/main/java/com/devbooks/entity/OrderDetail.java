package com.devbooks.entity;

import jakarta.persistence.*;
// import lombok.Data; // ❌ XÓA
import lombok.Getter;   // ✅ THÊM
import lombok.Setter;   // ✅ THÊM
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

// @Data // ❌ XÓA
@Getter   // ✅ THÊM
@Setter   // ✅ THÊM
@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Double pricePerUnit;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Book book;

    // ✅ THÊM: Tự viết toString() để phá vỡ vòng lặp
    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", pricePerUnit=" + pricePerUnit +
                ", bookId=" + (book != null ? book.getId() : null) +
                ", orderId=" + (order != null ? order.getId() : null) +
                '}';
        // Chỉ in ID, KHÔNG in toàn bộ Object
    }
}