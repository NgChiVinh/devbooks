package com.devbooks.repository;

import com.devbooks.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategoryId(Long categoryId);
    List<Book> findByTitleContainingIgnoreCase(String keyword);
}