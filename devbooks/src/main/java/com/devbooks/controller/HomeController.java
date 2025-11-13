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
// ✅ THÊM CÁC IMPORT CHO PHÂN TRANG
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HomeController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Xử lý trang chủ
     */
    @GetMapping("/")
    public String home(Model model) {
        List<Book> newestBooks = bookService.getNewestBooks();
        List<Book> topSellingBooks = bookService.getTopSellingBooks();
        List<Category> categoryList = categoryService.getAllCategories();

        model.addAttribute("newestBooks", newestBooks);
        model.addAttribute("topSellingBooks", topSellingBooks);
        model.addAttribute("categories", categoryList);

        return "user/index"; // Đảm bảo file index.html nằm trong templates/user/
    }

    /**
     * Xử lý trang "Sản phẩm" (ĐÃ CÓ PHÂN TRANG 12 SÁCH)
     */
    @GetMapping("/products")
    public String showAllProducts(
            Model model,
            @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 12);

        // ✅ SỬA LỖI: Gọi hàm phân trang
        Page<Book> bookPage = bookService.getAllBooks(pageable);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("categories", categoryService.getAllCategories());

        // ✅ SỬA LỖI: Gửi đối tượng Page (chứa sách)
        model.addAttribute("bookPage", bookPage);
        // ❌ KHÔNG GỬI "books" (vì hàm này không còn trả về List<Book> nữa)

        model.addAttribute("currentPage", page);
        model.addAttribute("activeCategory", null);

        return "user/products";
    }

    /**
     * Xử lý trang chi tiết sách (Giữ nguyên)
     */
    @GetMapping("/book/{id}")
    public String bookDetail(@PathVariable("id") Long id, Model model) {
        // ... (Code này đã đúng)
        return "user/book-detail";
    }

    /**
     * Xử lý lọc sách theo danh mục (ĐÃ CÓ PHÂN TRANG)
     */
    @GetMapping("/category/{id}")
    public String booksByCategory(
            @PathVariable("id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 12);
        // ✅ SỬA LỖI: Gọi hàm phân trang
        Page<Book> bookPage = bookService.getBooksByCategoryId(categoryId, pageable);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            // ... (Code tạo pageNumbers)
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("bookPage", bookPage); // ✅ Gửi Page
        model.addAttribute("currentPage", page);
        model.addAttribute("activeCategory", categoryId);

        return "user/products";
    }

    /**
     * Xử lý tìm kiếm sách (ĐÃ CÓ PHÂN TRANG)
     */
    @GetMapping("/search")
    public String searchBooks(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 12);
        // ✅ SỬA LỖI: Gọi hàm phân trang
        Page<Book> bookPage = bookService.searchBooks(keyword, pageable);

        int totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            // ... (Code tạo pageNumbers)
        }

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("bookPage", bookPage); // ✅ Gửi Page
        model.addAttribute("currentPage", page);
        model.addAttribute("searchKeyword", keyword);

        return "user/products";
    }
}