package com.devbooks.cart; // (Hoặc package entity của bạn nếu bạn di chuyển nó)

import com.devbooks.entity.Book;
import com.devbooks.entity.Cart; // Sẽ hết báo lỗi sau khi bạn tạo file Cart.java
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}