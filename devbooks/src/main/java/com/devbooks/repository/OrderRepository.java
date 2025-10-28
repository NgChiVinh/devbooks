package com.devbooks.repository;

import com.devbooks.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // <-- Thêm import này
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // (Code cũ của bạn)
    List<Order> findByUserId(Long userId);

    // (Code cũ, dùng cho trang danh sách)
    @Query("SELECT o FROM Order o JOIN FETCH o.user")
    List<Order> findAllWithUser();

    // === THÊM PHƯƠNG THỨC MỚI NÀY VÀO ===
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " + // Dùng LEFT JOIN để lấy đơn hàng ngay cả khi không có chi tiết
            "LEFT JOIN FETCH od.book " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
}