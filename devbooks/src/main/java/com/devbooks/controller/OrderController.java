package com.devbooks.controller;

import com.devbooks.cart.CartService;
import com.devbooks.entity.User;
import com.devbooks.service.OrderService;
import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    // (CartService vẫn có thể được giữ lại ở đây nếu bạn cần
    // hiển thị trang giỏ hàng, nhưng không truyền vào createOrder)
    @Autowired
    private CartService cartService;

    /**
     * Xử lý POST request khi người dùng nhấn "Xác nhận Đặt hàng"
     * URL: POST /order/submit
     */
    @PostMapping("/order/submit")
    public String submitOrder(
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("phoneNumber") String phoneNumber,
            Principal principal) {

        // 1. Lấy thông tin người dùng đang đăng nhập
        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. ✅ SỬA LỖI: Gọi hàm createOrder với 3 tham số (đã xóa cartService)
        orderService.createOrder(currentUser, shippingAddress, phoneNumber);

        // 3. ✅ SỬA LỖI: Xóa dòng cartService.clearCart() ở đây
        // (Vì OrderService đã tự động xóa giỏ hàng sau khi tạo đơn)

        // 4. Chuyển hướng đến trang thông báo thành công
        return "redirect:/order/success";
    }

    /**
     * Hiển thị trang đặt hàng thành công
     * URL: GET /order/success
     */
    @GetMapping("/order/success")
    public String orderSuccess() {
        return "user/order-success";
    }
}