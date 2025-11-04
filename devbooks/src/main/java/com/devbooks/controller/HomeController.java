package com.devbooks.controller;

import com.devbooks.entity.Book;
import com.devbooks.entity.Category;
import com.devbooks.service.BookService;
import com.devbooks.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Xử lý trang chủ
     * URL: GET /
     * === ĐÃ CẬP NHẬT ĐỂ LẤY SÁCH MỚI NHẤT ===
     */
    @GetMapping("/")
    public String home(Model model) {
        // 1. Lấy 5 cuốn sách mới nhất
        List<Book> bookList = bookService.getNewestBooks();

        // 2. Lấy tất cả danh mục
        List<Category> categoryList = categoryService.getAllCategories();

        // 3. Gửi ra view
        model.addAttribute("books", bookList); // Gửi sách mới nhất
        model.addAttribute("categories", categoryList);

        return "index"; // Trả về file index.html
    }

    /**
     * Xử lý trang chi tiết sách (chức năng 9)
     * URL: GET /book/{id}
     */
    @GetMapping("/book/{id}")
    public String bookDetail(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        model.addAttribute("book", book);

        return "user/book-detail";
    }

    /**
     * Xử lý trang lọc sách theo danh mục (chức năng 6)
     * URL: GET /category/{id}
     */
    @GetMapping("/category/{id}")
    public String booksByCategory(@PathVariable("id") Long categoryId, Model model) {
        List<Book> bookList = bookService.getBooksByCategoryId(categoryId);
        List<Category> categoryList = categoryService.getAllCategories();

        model.addAttribute("books", bookList);
        model.addAttribute("categories", categoryList);

        return "index";
    }

    /**
     * Xử lý tìm kiếm sách (chức năng 8)
     * URL: GET /search
     */
    @GetMapping("/search")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {

        List<Book> bookList = bookService.searchBooks(keyword);
        List<Category> categoryList = categoryService.getAllCategories();

        model.addAttribute("books", bookList);
        model.addAttribute("categories", categoryList);
        model.addAttribute("searchKeyword", keyword);

        return "index";
    }
}