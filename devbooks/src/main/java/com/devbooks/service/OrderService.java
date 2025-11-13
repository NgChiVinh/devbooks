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
import com.devbooks.entity.Cart; // ✅ Thêm import

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

    // ✅ === BƯỚC 1: TIÊM (INJECT) CART SERVICE ===
    @Autowired
    private CartService cartService;

    // (Phương thức cũ của bạn)
    public List<Order> getAllOrders() {
        return orderRepository.findAllWithUser();
    }

    // (Phương thức cũ của bạn)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findByIdWithDetails(id);
    }

    // ✅ === BƯỚC 2: SỬA HÀM CREATEORDER ===
    @Transactional
    public Order createOrder(User user, String shippingAddress, String phoneNumber) {

        // Lấy giỏ hàng từ service (chứ không phải truyền vào)
        Cart cart = cartService.getCartByUser(user);

        // 1. Tạo đối tượng Order chính
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cartService.getTotalPrice(user)); // Lấy tổng tiền từ user
        order.setStatus("Chờ xử lý");
        order.setShippingAddress(shippingAddress);
        // (Bạn có thể thêm trường sđt vào Entity Order nếu muốn và set nó ở đây)

        Order savedOrder = orderRepository.save(order);

        // 2. Tạo các chi tiết đơn hàng (Order Details)
        for (CartItem item : cart.getItems()) { // Lấy item từ 'cart'
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
                throw new RuntimeException("Sách " + book.getTitle() + " đã hết hàng!");
            }
            book.setStockQuantity(newStock);
            bookRepository.save(book);
        }

        // 4. ✅ QUAN TRỌNG: Xóa giỏ hàng sau khi đã đặt hàng
        cartService.clearCart(cart);

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

}