package com.devbooks.service;

import com.devbooks.entity.Book;
import com.devbooks.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

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
}