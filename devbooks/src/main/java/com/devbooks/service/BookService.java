package com.devbooks.service;

import com.devbooks.entity.Book;
import com.devbooks.repository.BookRepository;
import com.devbooks.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// ✅ THÊM 2 IMPORT NÀY
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // (Bạn đã có)

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private Cloudinary cloudinary;

    // ✅ SỬA: Trả về Page<Book> và nhận Pageable
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    // ✅ THÊM LẠI: Hàm getAllBooks() (không phân trang) cho Admin
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Lấy thông tin một cuốn sách qua ID (Giữ nguyên)
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // ✅ SỬA: Trả về Page<Book> và nhận Pageable
    public Page<Book> getBooksByCategoryId(Long categoryId, Pageable pageable) {
        return bookRepository.findByCategoryId(categoryId, pageable);
    }

    // Thêm hoặc cập nhật một cuốn sách (Giữ nguyên)
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Xóa một cuốn sách (Giữ nguyên)
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // ✅ SỬA: Trả về Page<Book> và nhận Pageable
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    /**
     * Lấy 5 cuốn sách mới nhất (Giữ nguyên)
     */
    public List<Book> getNewestBooks() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        return bookRepository.findAll(pageable).getContent();
    }

    /**
     * Lấy 5 cuốn sách bán chạy nhất (Giữ nguyên)
     */
    public List<Book> getTopSellingBooks() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Long> topBookIds = orderDetailRepository.findTopSellingBookIds(pageable);

        return topBookIds.stream()
                .map(id -> bookRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Hàm upload ảnh (Giữ nguyên)
     */
    public String uploadCoverImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "devbooks_uploads"));
            String secureUrl = (String) uploadResult.get("secure_url");
            return secureUrl;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể upload ảnh: " + e.getMessage());
        }
    }
}