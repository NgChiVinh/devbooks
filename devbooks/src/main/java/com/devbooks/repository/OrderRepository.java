package com.devbooks.repository;

import com.devbooks.entity.Order;
import com.devbooks.entity.User; // ✅ Phải có import này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // (Code cũ của bạn, có thể xóa nếu không dùng)
    List<Order> findByUserId(Long userId);

    // ✅ HÀM MỚI (CHO BƯỚC 1: HIỂN THỊ CHI TIẾT)
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.book " +
            "WHERE o.user = :user " +
            "ORDER BY o.orderDate DESC")
    List<Order> findOrdersByUserWithDetails(@Param("user") User user);

    // (Hàm cũ của Admin)
    @Query("SELECT o FROM Order o JOIN FETCH o.user")
    List<Order> findAllWithUser();

    // (Hàm cũ của Admin)
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.book " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
}