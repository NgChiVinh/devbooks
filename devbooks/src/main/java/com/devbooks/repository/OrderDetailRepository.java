package com.devbooks.repository;

import com.devbooks.entity.OrderDetail;
import org.springframework.data.domain.Pageable; // <-- Thêm import này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Thêm import này
import org.springframework.stereotype.Repository;

import java.util.List; // <-- Thêm import này

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    // === BẮT ĐẦU PHẦN CODE MỚI ===

    /**
     * Lấy danh sách ID của các sách bán chạy nhất.
     * 1. Nhóm theo 'book.id'
     * 2. Tính tổng 'quantity' (số lượng bán)
     * 3. Sắp xếp theo tổng số lượng giảm dần
     */
    @Query("SELECT od.book.id FROM OrderDetail od GROUP BY od.book.id ORDER BY SUM(od.quantity) DESC")
    List<Long> findTopSellingBookIds(Pageable pageable);

    // === KẾT THÚC PHẦN CODE MỚI ===
}