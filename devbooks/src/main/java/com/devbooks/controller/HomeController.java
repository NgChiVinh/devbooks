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
     * === ĐÃ CẬP NHẬT ĐỂ LẤY ĐỦ SÁCH MỚI VÀ BÁN CHẠY ===
     */
    @GetMapping("/")
    public String home(Model model) {
        // 1. Lấy 5 cuốn sách mới nhất
        List<Book> newestBooks = bookService.getNewestBooks();

        // 2. Lấy 5 cuốn sách bán chạy nhất
        List<Book> topSellingBooks = bookService.getTopSellingBooks();

        // 3. Lấy tất cả danh mục (cho Sidebar nếu cần)
        List<Category> categoryList = categoryService.getAllCategories();

        // 4. Gửi ra view
        model.addAttribute("newestBooks", newestBooks); // Sách mới nhất
        model.addAttribute("topSellingBooks", topSellingBooks); // Sách bán chạy
        model.addAttribute("categories", categoryList);

        // ✅ Sửa đường dẫn trả về
        return "index"; // Trả về file templates/user/index.html
    }

    // ✅ === HÀM MỚI CHO TRANG SẢN PHẨM ===
    /**
     * Xử lý trang "Sản phẩm" (Products)
     * URL: GET /products
     */
    @GetMapping("/products")
    public String showProductPage(Model model) {

        // 1. Lấy tất cả danh mục (để hiển thị sidebar)
        model.addAttribute("categories", categoryService.getAllCategories());

        // 2. Lấy tất cả sách
        model.addAttribute("books", bookService.getAllBooks());

        // 3. Trả về file HTML
        return "user/products"; // Trả về file templates/user/products.html
    }
    // ✅ === KẾT THÚC HÀM MỚI ===

    /**
     * Xử lý trang chi tiết sách (chức năng 9)
     * URL: GET /book/{id}
     */
    @GetMapping("/book/{id}")
    public String bookDetail(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        model.addAttribute("book", book);

        return "user/book-detail"; // Đường dẫn này đã đúng
    }

    /**
     * Xử lý lọc sách theo danh mục (chức năng 6)
     * URL: GET /category/{id}
     */
    @GetMapping("/category/{id}")
    public String booksByCategory(@PathVariable("id") Long categoryId, Model model) {
        List<Book> bookList = bookService.getBooksByCategoryId(categoryId);
        List<Category> categoryList = categoryService.getAllCategories();

        model.addAttribute("books", bookList);
        model.addAttribute("categories", categoryList);

        // Bạn có thể chọn trả về trang 'products' đã được lọc
        // Hoặc trả về 'index' (nhưng 'index' không có code để lọc)
        // ✅ Trả về trang products để hiển thị layout lọc
        return "user/products";
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

        // ✅ Trả về trang products để hiển thị kết quả tìm kiếm
        return "user/products";
    }
}