package com.devbooks.cart;

import com.devbooks.entity.Book;
import lombok.Data;

@Data // Tự động tạo getter, setter, equals, hashCode...
public class CartItem {

    private Book book;
    private int quantity;

    // Constructor
    public CartItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    // Phương thức tiện ích để lấy thành tiền
    public double getSubtotal() {
        return book.getPrice() * quantity;
    }
}