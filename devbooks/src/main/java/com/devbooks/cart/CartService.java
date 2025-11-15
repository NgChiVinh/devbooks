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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    // === HÀM GIỎ HÀNG DATABASE (USER ĐÃ ĐĂNG NHẬP) ===

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * ✅ HÀM ĐÃ CẬP NHẬT LOGIC TỒN KHO
     */
    @Transactional
    public void addBookToCart(User user, Long bookId, int quantityToAdd) {
        Cart cart = getCartByUser(user);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst();

        int currentQuantityInCart = existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int newQuantity = currentQuantityInCart + quantityToAdd;

        // === LOGIC KIỂM TRA TỒN KHO ===
        if (newQuantity > book.getStockQuantity()) {
            throw new RuntimeException("Số lượng tồn kho không đủ (Chỉ còn " + book.getStockQuantity() + " sản phẩm)");
        }
        // === KẾT THÚC KIỂM TRA ===

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setBook(book);
            newItem.setQuantity(newQuantity);
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }
    }

    public int getCartItemCount(User user) {
        Cart cart = getCartByUser(user);
        if (cart == null || cart.getItems() == null) { return 0; }
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public double getTotalPrice(User user) {
        Cart cart = getCartByUser(user);
        if (cart == null || cart.getItems() == null) { return 0.0; }
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

    public Set<CartItem> getItems(User user) {
        return getCartByUser(user).getItems();
    }

    // === HÀM GIỎ HÀNG SESSION (KHÁCH VÃNG LAI) ===

    public Map<Long, Integer> getSessionCart(HttpSession session) {
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    /**
     * ✅ HÀM ĐÃ CẬP NHẬT LOGIC TỒN KHO
     */
    public void addBookToSessionCart(HttpSession session, Long bookId, int quantityToAdd) {
        Map<Long, Integer> cart = getSessionCart(session);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));

        int currentQuantityInCart = cart.getOrDefault(bookId, 0);
        int newQuantity = currentQuantityInCart + quantityToAdd;

        // === LOGIC KIỂM TRA TỒN KHO ===
        if (newQuantity > book.getStockQuantity()) {
            throw new RuntimeException("Số lượng tồn kho không đủ (Chỉ còn " + book.getStockQuantity() + " sản phẩm)");
        }
        // === KẾT THÚC KIỂM TRA ===

        cart.put(bookId, newQuantity);
        session.setAttribute("cart", cart);
    }

    public int getSessionCartItemCount(HttpSession session) {
        Map<Long, Integer> cart = getSessionCart(session);
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    public List<CartItem> getSessionCartItems(HttpSession session) {
        Map<Long, Integer> sessionCart = getSessionCart(session);
        List<CartItem> cartItems = new ArrayList<>();
        if (sessionCart.isEmpty()) { return cartItems; }
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

    public double getSessionTotalPrice(HttpSession session) {
        List<CartItem> cartItems = getSessionCartItems(session);
        if (cartItems.isEmpty()) { return 0.0; }
        return cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getBook().getPrice())
                .sum();
    }

    // === HÀM CẬP NHẬT/XÓA (CHO TRANG CART.HTML) ===

    @Transactional
    public double updateItemQuantity(User user, Long bookId, int quantity) {
        Cart cart = getCartByUser(user);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy item"));

        if (quantity < 1) { quantity = 1; }

        // ✅ THÊM KIỂM TRA TỒN KHO KHI CẬP NHẬT
        if (quantity > item.getBook().getStockQuantity()) {
            throw new RuntimeException("Số lượng tồn kho không đủ (Chỉ còn " + item.getBook().getStockQuantity() + " sản phẩm)");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return item.getBook().getPrice() * quantity;
    }

    @Transactional
    public void removeItem(User user, Long bookId) {
        Cart cart = getCartByUser(user);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy item"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
    }

    public double updateSessionItemQuantity(HttpSession session, Long bookId, int quantity) {
        Map<Long, Integer> cart = getSessionCart(session);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách"));

        if (quantity < 1) { quantity = 1; }

        // ✅ THÊM KIỂM TRA TỒN KHO KHI CẬP NHẬT
        if (quantity > book.getStockQuantity()) {
            throw new RuntimeException("Số lượng tồn kho không đủ (Chỉ còn " + book.getStockQuantity() + " sản phẩm)");
        }

        cart.put(bookId, quantity);
        session.setAttribute("cart", cart);

        return book.getPrice() * quantity;
    }

    public void removeSessionItem(HttpSession session, Long bookId) {
        Map<Long, Integer> cart = getSessionCart(session);
        cart.remove(bookId);
        session.setAttribute("cart", cart);
    }
}