package com.devbooks.service;

import com.devbooks.entity.Order;
import com.devbooks.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        // Sửa lại dòng này, gọi phương thức mới
        return orderRepository.findAllWithUser();

    }
    // Lấy một đơn hàng bằng ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findByIdWithDetails(id); // Gọi phương thức mới
    }
}