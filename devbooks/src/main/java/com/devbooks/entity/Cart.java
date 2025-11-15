package com.devbooks.entity;

import com.devbooks.cart.CartItem; // Đảm bảo import đúng
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items = new HashSet<>();

    // ✅ THÊM: Tự viết toString() để phá vỡ vòng lặp
    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", numberOfItems=" + (items != null ? items.size() : 0) +
                '}';
        // Chỉ in ID của User và số lượng Item, KHÔNG in toàn bộ Object
    }
}