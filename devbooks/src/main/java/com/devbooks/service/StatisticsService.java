package com.devbooks.service;

import com.devbooks.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    @Autowired
    private OrderRepository orderRepository;

    // (Bạn sẽ cần thêm câu lệnh Query tùy chỉnh vào OrderRepository)

    // Lấy tổng doanh thu
    public Double getTotalRevenue() {
        // Tạm thời tính đơn giản, sau này sẽ dùng query
        return orderRepository.findAll().stream()
                .mapToDouble(order -> order.getTotalAmount())
                .sum();
    }

    // Lấy tổng số đơn hàng
    public long getTotalOrders() {
        return orderRepository.count();
    }

    // (Logic lấy top sách bán chạy sẽ phức tạp hơn, làm sau)
}