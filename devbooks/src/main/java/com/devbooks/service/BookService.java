package com.devbooks.service;

import com.devbooks.entity.Book;
import com.devbooks.repository.BookRepository;
import com.devbooks.repository.OrderDetailRepository; // <-- THÊM IMPORT NÀY
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // <-- THÊM IMPORT NÀY

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // --- THÊM DEPENDENCY MỚI ---
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    // --- KẾT THÚC PHẦN THÊM MỚI ---

    // Lấy tất cả sách
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Lấy thông tin một cuốn sách qua ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // Tìm sách theo danh mục
    public List<Book> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    // Thêm hoặc cập nhật một cuốn sách
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Xóa một cuốn sách
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Tìm kiếm sách theo tiêu đề
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    /**
     * Lấy 5 cuốn sách mới nhất (chức năng 7)
     */
    public List<Book> getNewestBooks() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        return bookRepository.findAll(pageable).getContent();
    }

    // === BẮT ĐẦU PHẦN CODE MỚI ===

    /**
     * Lấy 5 cuốn sách bán chạy nhất (chức năng 7)
     */
    public List<Book> getTopSellingBooks() {
        // Lấy 5 ID sách bán chạy nhất từ repository
        Pageable pageable = PageRequest.of(0, 5);
        List<Long> topBookIds = orderDetailRepository.findTopSellingBookIds(pageable);

        // Từ các ID, lấy thông tin sách đầy đủ
        return topBookIds.stream()
                .map(id -> bookRepository.findById(id)) // Tìm sách
                .filter(Optional::isPresent) // Lọc bỏ các sách không tìm thấy (nếu có)
                .map(Optional::get) // Lấy đối tượng Book từ Optional
                .collect(Collectors.toList()); // Thu thập lại thành List<Book>
    }

    // === KẾT THÚC PHẦN CODE MỚI ===
}