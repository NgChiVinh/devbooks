package com.devbooks.repository;

import com.devbooks.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// ✅ THÊM 2 IMPORT NÀY
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ✅ SỬA: Trả về Page<Book> và nhận Pageable
    Page<Book> findByCategoryId(Long categoryId, Pageable pageable);

    // ✅ SỬA: Trả về Page<Book> và nhận Pageable
    Page<Book> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}