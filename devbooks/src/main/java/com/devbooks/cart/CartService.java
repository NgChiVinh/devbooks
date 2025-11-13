package com.devbooks.cart;

import com.devbooks.entity.Book;
import com.devbooks.entity.Cart;
import com.devbooks.entity.User;
import com.devbooks.repository.BookRepository;
import com.devbooks.repository.CartItemRepository;
import com.devbooks.repository.CartRepository;
import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
// ✅ THÊM 2 IMPORT NÀY
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserService userService;

    // === CÁC HÀM XỬ LÝ GIỎ HÀNG VĨNH VIỄN (DATABASE) ===

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public void addBookToCart(User user, Long bookId, int quantity) {
        Cart cart = getCartByUser(user);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }
    }

    public int getCartItemCount(User user) {
        Cart cart = getCartByUser(user);
        if (cart == null || cart.getItems() == null) {
            return 0;
        }
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public double getTotalPrice(User user) {
        Cart cart = getCartByUser(user);
        if (cart == null || cart.getItems() == null) {
            return 0.0;
        }
        return cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getBook().getPrice())
                .sum();
    }

    @Transactional
    public void clearCart(Cart cart) {
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // Hàm này được OrderService cần đến
    public Set<CartItem> getItems(User user) {
        return getCartByUser(user).getItems();
    }

    // === CÁC HÀM MỚI CHO GIỎ HÀNG KHÁCH (SESSION) ===

    /**
     * Lấy giỏ hàng từ Session, nếu chưa có thì tạo mới
     */
    public Map<Long, Integer> getSessionCart(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    /**
     * Thêm sách vào giỏ hàng Session (cho khách)
     */
    public void addBookToSessionCart(HttpSession session, Long bookId, int quantity) {
        Map<Long, Integer> cart = getSessionCart(session);
        cart.put(bookId, cart.getOrDefault(bookId, 0) + quantity);
        session.setAttribute("cart", cart);
    }

    /**
     * Đếm số lượng trong giỏ hàng Session
     */
    public int getSessionCartItemCount(HttpSession session) {
        Map<Long, Integer> cart = getSessionCart(session);
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * ✅ HÀM MỚI (BỊ THIẾU): Lấy danh sách các CartItem đầy đủ từ Session
     */
    public List<CartItem> getSessionCartItems(HttpSession session) {
        Map<Long, Integer> sessionCart = getSessionCart(session);
        List<CartItem> cartItems = new ArrayList<>();

        if (sessionCart.isEmpty()) {
            return cartItems;
        }

        List<Long> bookIds = new ArrayList<>(sessionCart.keySet());
        List<Book> books = bookRepository.findAllById(bookIds);

        for (Book book : books) {
            int quantity = sessionCart.get(book.getId());
            CartItem item = new CartItem();
            item.setBook(book);
            item.setQuantity(quantity);
            cartItems.add(item);
        }
        return cartItems;
    }

    /**
     * ✅ HÀM MỚI (BỊ THIẾU): Tính tổng tiền cho giỏ hàng Session
     */
    public double getSessionTotalPrice(HttpSession session) {
        List<CartItem> cartItems = getSessionCartItems(session);
        if (cartItems.isEmpty()) {
            return 0.0;
        }

        return cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getBook().getPrice())
                .sum();
    }
}