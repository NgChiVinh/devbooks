package com.devbooks.controller;

import com.devbooks.cart.CartService;
import com.devbooks.entity.User;
import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication; // (Import này hiện không dùng, nhưng có thể giữ lại)
import org.springframework.security.core.context.SecurityContextHolder; // (Import này hiện không dùng)
import java.security.Principal;


@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    // === PHẦN THÊM VÀO ===
    @Autowired
    private UserService userService;
    // === KẾT THÚC PHẦN THÊM VÀO ===

    /**
     * Xử lý POST request để thêm sản phẩm vào giỏ
     */
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("bookId") Long bookId,
                            @RequestParam("quantity") int quantity) {

        cartService.addToCart(bookId, quantity);

        // Chuyển hướng đến trang xem giỏ hàng sau khi thêm
        return "redirect:/cart";
    }

    /**
     * Xử lý GET request để hiển thị trang giỏ hàng
     */
    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());

        return "user/cart"; // Trả về file user/cart.html
    }

    /**
     * Xử lý POST request để cập nhật số lượng
     */
    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("bookId") Long bookId,
                             @RequestParam("quantity") int quantity) {

        // Gọi service để cập nhật số lượng
        cartService.updateItem(bookId, quantity);

        return "redirect:/cart";
    }

    /**
     * Xử lý POST request để xóa sản phẩm
     */
    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("bookId") Long bookId) {

        // Gọi service để xóa
        cartService.removeItem(bookId);

        return "redirect:/cart";
    }

    /**
     * Xử lý GET request để hiển thị trang Thanh toán
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {

        // Lấy thông tin người dùng đang đăng nhập
        if (principal != null) {
            String email = principal.getName();
            // Bây giờ dòng này sẽ hoạt động vì userService đã được tiêm vào
            User currentUser = userService.findByEmail(email)
                    .orElse(null);
            model.addAttribute("currentUser", currentUser);
        }

        // Gửi thông tin giỏ hàng sang trang checkout
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());

        return "user/checkout"; // Trả về file user/checkout.html
    }
}