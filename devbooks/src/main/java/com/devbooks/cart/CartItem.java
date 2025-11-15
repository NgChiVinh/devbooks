package com.devbooks.cart;

import com.devbooks.entity.Book;
import com.devbooks.entity.Cart;
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

    // ✅ THÊM: Tự viết toString() để phá vỡ vòng lặp
    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", bookId=" + (book != null ? book.getId() : null) +
                ", cartId=" + (cart != null ? cart.getId() : null) +
                '}';
        // Chỉ in ID, KHÔNG in toàn bộ Object
    }
}