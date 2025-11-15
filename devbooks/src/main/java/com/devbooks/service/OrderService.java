package com.devbooks.service;

import com.devbooks.cart.CartItem;
import com.devbooks.cart.CartService;
import com.devbooks.entity.Book;
import com.devbooks.entity.Order;
import com.devbooks.entity.OrderDetail;
import com.devbooks.entity.User;
import com.devbooks.repository.BookRepository;
import com.devbooks.repository.OrderDetailRepository;
import com.devbooks.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.devbooks.entity.Cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CartService cartService;

    // (Hàm getAllOrders() và getOrderById() - Giữ nguyên)
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithUser();
    }
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findByIdWithDetails(id);
    }

    // (Hàm createOrder() - Giữ nguyên, đã trừ kho)
    @Transactional
    public Order createOrder(User user, String shippingAddress,String city , String phoneNumber, String paymentMethod) {
        Cart cart = cartService.getCartByUser(user);
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cartService.getTotalPrice(user));
        order.setStatus("Chờ xử lý");
        order.setShippingAddress(shippingAddress);
        order.setCity(city);
        order.setPaymentMethod(paymentMethod);
        Order savedOrder = orderRepository.save(order);

        for (CartItem item : cart.getItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setBook(item.getBook());
            detail.setQuantity(item.getQuantity());
            detail.setPricePerUnit(item.getBook().getPrice());
            orderDetailRepository.save(detail);

            Book book = item.getBook();
            int newStock = book.getStockQuantity() - item.getQuantity();
            if (newStock < 0) {
                throw new RuntimeException("Sách " + book.getTitle() + " đã hết hàng!");
            }
            book.setStockQuantity(newStock);
            bookRepository.save(book);
        }
        cartService.clearCart(cart);
        return savedOrder;
    }

    // (Hàm updateOrderStatus() - Giữ nguyên)
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    /**
     * HÀM (CHO BƯỚC 1): Lấy tất cả đơn hàng VÀ chi tiết
     */
    public List<Order> findOrdersByUser(User user) {
        return orderRepository.findOrdersByUserWithDetails(user);
    }

    /**
     * ✅ HÀM ĐÃ ĐƯỢC NÂNG CẤP (TỐI ƯU 3)
     * Xử lý Hủy đơn hàng VÀ Hoàn trả tồn kho
     */
    @Transactional
    public void cancelOrder(Long orderId, User user) {

        // 1. Tìm đơn hàng (PHẢI dùng findByIdWithDetails để tải chi tiết sách)
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // 2. Kiểm tra bảo mật
        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Bạn không có quyền hủy đơn hàng này");
        }

        // 3. Kiểm tra trạng thái
        if (!"Chờ xử lý".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("Đơn hàng đã được xử lý, không thể hủy");
        }

        // 4. ✅ HOÀN TRẢ TỒN KHO
        for (OrderDetail detail : order.getOrderDetails()) {
            Book book = detail.getBook();
            if (book != null) {
                // Cộng trả lại số lượng đã mua
                book.setStockQuantity(book.getStockQuantity() + detail.getQuantity());
                bookRepository.save(book);
            }
        }
        // 4. ✅ KẾT THÚC HOÀN TRẢ TỒN KHO

        // 5. Cập nhật trạng thái
        order.setStatus("Đã hủy");
        orderRepository.save(order);
    }
}