package com.devbooks.controller;

import com.devbooks.cart.CartService;
import com.devbooks.entity.Order;
import com.devbooks.entity.User;
import com.devbooks.service.OrderService;
import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    /**
     * Hiển thị trang Thanh toán
     * URL: GET /order/checkout
     */
    @GetMapping("/order/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("cartItems", cartService.getItems(user));
        model.addAttribute("totalAmount", cartService.getTotalPrice(user));
        model.addAttribute("user", user);

        return "user/checkout";
    }

    /**
     * ✅ Xử lý POST request khi người dùng nhấn "Xác nhận Đặt hàng"
     * URL: POST /order/submit
     */
    @PostMapping("/order/submit")
    public String submitOrder(
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("city") String city,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("paymentMethod") String paymentMethod,
            Principal principal,
            RedirectAttributes redirectAttributes) { // ✅ Dùng RedirectAttributes

        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        try { // ✅ THÊM KHỐI TRY
            // 1. Nếu tồn kho đủ, tạo đơn hàng
            orderService.createOrder(currentUser, shippingAddress, city, phoneNumber, paymentMethod);

            // THÀNH CÔNG: Chuyển hướng đến trang "Đơn hàng của tôi"
            redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được tạo thành công!");
            return "redirect:/my-orders";

        } catch (RuntimeException e) { // ✅ THÊM KHỐI CATCH

            // THẤT BẠI (Lỗi tồn kho)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

            // Chuyển hướng người dùng TRỞ LẠI trang Checkout để sửa
            return "redirect:/order/checkout";
        }
    }

    /**
     * Hiển thị trang đặt hàng thành công (Giữ lại, không dùng)
     */
    @GetMapping("/order/success")
    public String orderSuccess() {
        return "user/order-success";
    }

    /**
     * Hiển thị trang "Đơn Hàng Của Tôi" (GET /my-orders)
     */
    @GetMapping("/my-orders")
    public String showMyOrdersPage(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderService.findOrdersByUser(user);

        model.addAttribute("orders", orders);

        return "user/my-orders";
    }

    /**
     * Xử lý Hủy đơn hàng (POST /order/cancel/{id})
     */
    @PostMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Long orderId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            orderService.cancelOrder(orderId, user);
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn hàng #" + orderId + " thành công.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hủy đơn thất bại: " + e.getMessage());
        }

        return "redirect:/my-orders";
    }
}