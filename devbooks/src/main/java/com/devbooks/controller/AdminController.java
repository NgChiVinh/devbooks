package com.devbooks.controller;

import com.devbooks.entity.Book;
import com.devbooks.entity.Category;
import com.devbooks.entity.Order;
import com.devbooks.entity.User;
import com.devbooks.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Cần import này cho List.of() và Optional

@Controller
@RequestMapping("/admin")
public class AdminController {

    // --- TẬP TRUNG AUTOWIRED LÊN ĐẦU ---
    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StatisticsService statisticsService;
    // --- KẾT THÚC AUTOWIRED ---


    // === DASHBOARD ===
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("totalRevenue", statisticsService.getTotalRevenue());
        model.addAttribute("totalOrders", statisticsService.getTotalOrders());
        model.addAttribute("topSellingBooks", bookService.getTopSellingBooks());
        return "admin/dashboard";
    }


    // --- QUẢN LÝ SÁCH ---
    @GetMapping("/books")
    public String listBooks(Model model) {
        List<Book> bookList = bookService.getAllBooks();
        model.addAttribute("books", bookList);
        return "admin/book-list";
    }

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/add-book";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute("book") Book book) {
        book.setCreatedAt(LocalDateTime.now());
        bookService.saveBook(book);
        return "redirect:/admin/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/admin/books";
    }

    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("book", book);
        return "admin/edit-book";
    }

    @PostMapping("/books/edit/{id}")
    public String updateBook(@PathVariable("id") Long id, @ModelAttribute("book") Book bookDetails) {
        Book existingBook = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setAuthor(bookDetails.getAuthor());
        existingBook.setPrice(bookDetails.getPrice());
        existingBook.setStockQuantity(bookDetails.getStockQuantity());
        existingBook.setDescription(bookDetails.getDescription());
        existingBook.setCoverImageUrl(bookDetails.getCoverImageUrl());
        existingBook.setCategory(bookDetails.getCategory());
        bookService.saveBook(existingBook);
        return "redirect:/admin/books";
    }

    // --- QUẢN LÝ DANH MỤC ---
    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category-list";
    }

    @GetMapping("/categories/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/add-category";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute("category") Category category) {
        categoryService.createCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String showEditCategoryForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    @PostMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id, @ModelAttribute("category") Category categoryDetails) {
        Category existingCategory = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        existingCategory.setName(categoryDetails.getName());
        categoryService.createCategory(existingCategory);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    // --- QUẢN LÝ NGƯỜI DÙNG ---
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);
        return "admin/user-list";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        model.addAttribute("user", user);
        return "admin/edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUserRole(@PathVariable("id") Long id, @RequestParam("role") String role) {

        userService.updateUserRole(id, role);

        return "redirect:/admin/users";
    }

    // --- QUẢN LÝ ĐƠN HÀNG ---
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

    // === BẮT ĐẦU CODE MỚI CHO SỬA TRẠNG THÁI ĐƠN HÀNG ===

    /**
     * 1. Hiển thị form để sửa trạng thái đơn hàng
     * URL: GET /admin/orders/edit/{id}
     */
    @GetMapping("/orders/edit/{id}")
    public String showEditOrderForm(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));

        // Gửi danh sách các trạng thái có thể chọn
        // Lưu ý: List.of yêu cầu Java 9+ (Bạn đang dùng Java 21 nên OK)
        model.addAttribute("statuses", List.of("Chờ xử lý", "Đang giao", "Đã giao", "Đã hủy"));
        model.addAttribute("order", order);
        return "admin/edit-order";
    }

    /**
     * 2. Xử lý việc cập nhật trạng thái
     * URL: POST /admin/orders/edit/{id}
     */
    @PostMapping("/orders/edit/{id}")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {

        orderService.updateOrderStatus(id, status);

        return "redirect:/admin/orders";
    }

    // === KẾT THÚC CODE MỚI ===
}