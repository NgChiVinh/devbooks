package com.devbooks.controller;

import com.devbooks.entity.Book;
import com.devbooks.entity.Category; // <-- Thêm import này
import com.devbooks.service.BookService;
import com.devbooks.service.CategoryService; // <-- Thêm import này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.devbooks.entity.User; // Thêm import này
import com.devbooks.service.UserService; // Thêm import này
import com.devbooks.entity.Order;
import com.devbooks.service.OrderService;

import java.time.LocalDateTime; // <-- Thêm import này
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BookService bookService;

    @Autowired // <-- Thêm Autowired cho CategoryService
    private CategoryService categoryService;

    @Autowired // <-- Thêm Autowired cho OrderService
    private OrderService orderService;

    // (Phương thức listBooks của bạn vẫn giữ nguyên ở đây...)
    @GetMapping("/books")
    public String listBooks(Model model) {
        List<Book> bookList = bookService.getAllBooks();
        model.addAttribute("books", bookList);
        return "admin/book-list";
    }

    // === BẮT ĐẦU PHẦN CODE MỚI ===

    // 1. Xử lý GET request tới /admin/books/add -> Hiển thị form
    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        // Tạo một đối tượng Book rỗng để form có thể binding dữ liệu
        model.addAttribute("book", new Book());

        // Lấy tất cả danh mục để hiển thị trong dropdown
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "admin/add-book"; // Trả về file add-book.html
    }

    // 2. Xử lý POST request tới /admin/books/add -> Lưu sách vào DB
    @PostMapping("/books/add")
    public String addBook(@ModelAttribute("book") Book book) {
        // Gán ngày tạo là thời điểm hiện tại
        book.setCreatedAt(LocalDateTime.now());

        // Gọi service để lưu sách
        bookService.saveBook(book);

        // Chuyển hướng về trang danh sách sách sau khi thêm thành công
        return "redirect:/admin/books";
    }

    // Xử lý POST request tới /admin/books/delete/{id}
    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/admin/books";
    }
    // 1. Hiển thị form để sửa thông tin sách
    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable("id") Long id, Model model) {
        // Lấy thông tin sách cần sửa từ database
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        // Lấy tất cả danh mục để hiển thị trong dropdown
        model.addAttribute("categories", categoryService.getAllCategories());
        // Gửi thông tin sách hiện tại tới form
        model.addAttribute("book", book);

        return "admin/edit-book"; // Trả về file edit-book.html
    }

    // 2. Xử lý việc cập nhật thông tin sách
    @PostMapping("/books/edit/{id}")
    public String updateBook(@PathVariable("id") Long id, @ModelAttribute("book") Book bookDetails) {
        // Lấy sách gốc từ DB
        Book existingBook = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        // Cập nhật các thông tin từ form
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setPrice(bookDetails.getPrice());
        existingBook.setStockQuantity(bookDetails.getStockQuantity());
        existingBook.setDescription(bookDetails.getDescription());
        existingBook.setCoverImageUrl(bookDetails.getCoverImageUrl());
        existingBook.setCategory(bookDetails.getCategory());

        // Lưu lại sách đã được cập nhật
        bookService.saveBook(existingBook);

        return "redirect:/admin/books";
    }
    // === BẮT ĐẦU PHẦN CODE MỚI CHO QUẢN LÝ DANH MỤC ===

    // 1. Hiển thị danh sách tất cả danh mục
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category-list";
    }

    // 2. Hiển thị form để thêm danh mục mới
    @GetMapping("/categories/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/add-category";
    }

    // 3. Xử lý việc lưu danh mục mới
    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute("category") Category category) {
        categoryService.createCategory(category);
        return "redirect:/admin/categories";
    }

    // 1. Hiển thị form để sửa danh mục
    @GetMapping("/categories/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    // 2. Xử lý việc cập nhật danh mục
    @PostMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id, @ModelAttribute("category") Category categoryDetails) {
        Category existingCategory = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        existingCategory.setName(categoryDetails.getName());
        categoryService.createCategory(existingCategory); // Dùng lại phương thức save
        return "redirect:/admin/categories";
    }

    // 3. Xử lý việc xóa danh mục
    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }
    @Autowired // Thêm Autowired cho UserService
    private UserService userService;

    // ... (toàn bộ code quản lý sách và danh mục của bạn)

    // === BẮT ĐẦU CODE MỚI CHO QUẢN LÝ NGƯỜI DÙNG ===

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);
        return "admin/user-list"; // Trả về file user-list.html
    }
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orderList = orderService.getAllOrders();
        model.addAttribute("orders", orderList);
        return "admin/order-list";
    }
    @GetMapping("/orders/{id}")
    public String viewOrderDetail(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));

        model.addAttribute("order", order);
        return "admin/order-detail";
    }
    // === KẾT THÚC PHẦN CODE MỚI ===
}