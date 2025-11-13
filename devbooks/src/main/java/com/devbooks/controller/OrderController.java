package com.devbooks.controller;

import com.devbooks.cart.CartService;
import com.devbooks.entity.User;
import com.devbooks.service.OrderService;
import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ THÊM IMPORT NÀY
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    // ✅ === HÀM MỚI: HIỂN THỊ TRANG CHECKOUT ===
    /**
     * Hiển thị trang Thanh toán
     * URL: GET /checkout
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        // Lấy user đã đăng nhập
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy giỏ hàng của user (từ DB) để hiển thị tóm tắt
        model.addAttribute("cartItems", cartService.getItems(user));
        model.addAttribute("totalAmount", cartService.getTotalPrice(user));

        return "user/checkout"; // Trả về file templates/user/checkout.html
    }
    // ✅ === KẾT THÚC HÀM MỚI ===

    /**
     * Xử lý POST request khi người dùng nhấn "Xác nhận Đặt hàng"
     * URL: POST /order/submit
     */
    @PostMapping("/order/submit")
    public String submitOrder(
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("phoneNumber") String phoneNumber,
            Principal principal) {

        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Gọi hàm createOrder (đã sửa lỗi)
        orderService.createOrder(currentUser, shippingAddress, phoneNumber);

        return "redirect:/order/success";
    }

    /**
     * Hiển thị trang đặt hàng thành công
     * URL: GET /order/success
     */
    @GetMapping("/order/success")
    public String orderSuccess() {
        return "user/order-success"; // Trả về file templates/user/order-success.html
    }
}