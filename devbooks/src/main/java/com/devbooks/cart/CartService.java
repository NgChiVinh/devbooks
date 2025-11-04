package com.devbooks.cart;

import com.devbooks.entity.Book;
import com.devbooks.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope; // <-- Rất quan trọng

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope // <-- Đánh dấu Service này sẽ được tạo mới cho mỗi phiên (session)
public class CartService {

    // Dùng Map để lưu giỏ hàng, với Key là ID của sách, Value là CartItem
    private Map<Long, CartItem> items = new HashMap<>();

    @Autowired
    private BookService bookService; // Cần BookService để lấy thông tin sách

    /**
     * Thêm sách vào giỏ hàng (hoặc tăng số lượng nếu đã có)
     */
    public void addToCart(Long bookId, int quantity) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        if (items.containsKey(bookId)) {
            // Nếu sách đã có trong giỏ, chỉ tăng số lượng
            CartItem existingItem = items.get(bookId);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Nếu sách chưa có, thêm mới vào giỏ
            items.put(bookId, new CartItem(book, quantity));
        }
    }

    /**
     * Lấy tất cả các món hàng trong giỏ
     */
    public Collection<CartItem> getItems() {
        return items.values();
    }

    /**
     * Xóa một món hàng khỏi giỏ
     */
    public void removeItem(Long bookId) {
        items.remove(bookId);
    }

    /**
     * Cập nhật lại số lượng của một món hàng
     */
    public void updateItem(Long bookId, int quantity) {
        if (items.containsKey(bookId)) {
            if (quantity <= 0) {
                // Nếu số lượng <= 0 thì xóa luôn
                items.remove(bookId);
            } else {
                // Ngược lại thì cập nhật số lượng
                items.get(bookId).setQuantity(quantity);
            }
        }
    }

    /**
     * Tính tổng tiền của giỏ hàng
     */
    public double getTotalPrice() {
        return items.values().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    /**
     * Lấy tổng số lượng sản phẩm trong giỏ
     */
    public int getCount() {
        return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    /**
     * Xóa sạch giỏ hàng (dùng sau khi đã đặt hàng)
     */
    public void clearCart() {
        items.clear();
    }
}