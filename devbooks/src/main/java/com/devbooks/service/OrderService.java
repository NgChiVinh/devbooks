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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // --- THÊM CÁC DEPENDENCY MỚI ---
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private BookRepository bookRepository;
    // --- KẾT THÚC PHẦN THÊM MỚI ---

    // (Phương thức cũ của bạn)
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithUser();
    }

    // (Phương thức cũ của bạn)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findByIdWithDetails(id);
    }

    // === BẮT ĐẦU PHẦN CODE MỚI ===

    @Transactional // Đảm bảo tất cả thao tác thành công hoặc thất bại cùng lúc
    public Order createOrder(User user, CartService cartService, String shippingAddress, String phoneNumber) {

        // 1. Tạo đối tượng Order chính
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cartService.getTotalPrice());
        order.setStatus("Chờ xử lý");
        order.setShippingAddress(shippingAddress);
        // (Bạn có thể thêm trường sđt vào Entity Order nếu muốn và set nó ở đây)

        Order savedOrder = orderRepository.save(order);

        // 2. Tạo các chi tiết đơn hàng (Order Details)
        for (CartItem item : cartService.getItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setBook(item.getBook());
            detail.setQuantity(item.getQuantity());
            detail.setPricePerUnit(item.getBook().getPrice());
            orderDetailRepository.save(detail);

            // 3. Cập nhật số lượng sách trong kho
            Book book = item.getBook();
            int newStock = book.getStockQuantity() - item.getQuantity();
            if (newStock < 0) {
                // Xử lý lỗi nếu hết hàng (nên kiểm tra trước khi đặt)
                throw new RuntimeException("Sách " + book.getTitle() + " đã hết hàng!");
            }
            book.setStockQuantity(newStock);
            bookRepository.save(book);
        }

        return savedOrder;
    }
    /**
    * Cập nhật trạng thái cho đơn hàng
     * */
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        orderRepository.save(order);
    }
    // === KẾT THÚC PHẦN CODE MỚI ===
}