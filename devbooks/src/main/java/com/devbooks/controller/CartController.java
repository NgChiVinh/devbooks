package com.devbooks.controller;

import com.devbooks.entity.User;
import com.devbooks.cart.CartService;
import com.devbooks.cart.CartItem;
import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    /**
     * API Endpoint để thêm sách vào giỏ
     * URL: POST /cart/add/{bookId}
     */
    @PostMapping("/add/{bookId}")
    @ResponseBody
    public ResponseEntity<?> addToCart(
            @PathVariable("bookId") Long bookId,
            Authentication authentication,
            HttpSession session
    ) {
        int totalItems = 0;

        try { // ✅ BỌC TOÀN BỘ LOGIC TRONG TRY...CATCH

            if (authentication != null && authentication.isAuthenticated()) {
                // === KỊCH BẢN 1: USER ĐÃ ĐĂNG NHẬP ===
                String email = authentication.getName();
                User user = userService.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
                cartService.addBookToCart(user, bookId, 1);
                totalItems = cartService.getCartItemCount(user);

            } else {
                // === KỊCH BẢN 2: KHÁCH VÃNG LAI ===
                cartService.addBookToSessionCart(session, bookId, 1);
                totalItems = cartService.getSessionCartItemCount(session);
            }

            // Trả về JSON thành công
            return ResponseEntity.ok(Map.of("success", true, "totalItems", totalItems));

        } catch (Exception e) {
            // ✅ NẾU CÓ BẤT KỲ LỖI GÌ (VD: NullPointerException), NÓ SẼ BỊ BẮT Ở ĐÂY
            e.printStackTrace(); // In lỗi Java ra Console IntelliJ
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Hiển thị trang "Xem Giỏ Hàng"
     * URL: GET /cart
     */
    @GetMapping
    public String showCartPage(Model model, Authentication authentication, HttpSession session) {

        List<CartItem> cartItems;
        double totalAmount;

        if (authentication != null && authentication.isAuthenticated()) {
            // === USER ĐÃ ĐĂNG NHẬP ===
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            cartItems = cartService.getItems(user).stream().toList();
            totalAmount = cartService.getTotalPrice(user);
        } else {
            // === KHÁCH VÃNG LAI ===
            cartItems = cartService.getSessionCartItems(session);
            totalAmount = cartService.getSessionTotalPrice(session);
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "user/cart";
    }
}