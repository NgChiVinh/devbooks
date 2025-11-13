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
import java.util.HashMap; // ✅ Thêm Import

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    /**
     * API Thêm vào giỏ
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

        try {
            if (authentication != null && authentication.isAuthenticated()) {
                // USER ĐÃ ĐĂNG NHẬP
                User user = userService.findByEmail(authentication.getName())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
                cartService.addBookToCart(user, bookId, 1);
                totalItems = cartService.getCartItemCount(user);
            } else {
                // KHÁCH VÃNG LAI
                cartService.addBookToSessionCart(session, bookId, 1);
                totalItems = cartService.getSessionCartItemCount(session);
            }
            return ResponseEntity.ok(Map.of("success", true, "totalItems", totalItems));

        } catch (Exception e) {
            e.printStackTrace();
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
            // USER ĐÃ ĐĂNG NHẬP
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            cartItems = cartService.getItems(user).stream().toList();
            totalAmount = cartService.getTotalPrice(user);
        } else {
            // KHÁCH VÃNG LAI
            cartItems = cartService.getSessionCartItems(session);
            totalAmount = cartService.getSessionTotalPrice(session);
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "user/cart";
    }

    // ✅ === HÀM MỚI: CẬP NHẬT SỐ LƯỢNG ===
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartQuantity(
            @RequestParam("bookId") Long bookId,
            @RequestParam("quantity") int quantity,
            Authentication authentication,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        double newSubtotal;
        int newTotalItems;
        double itemTotalPrice;

        try {
            if (authentication != null && authentication.isAuthenticated()) {
                // USER ĐÃ ĐĂNG NHẬP
                User user = userService.findByEmail(authentication.getName()).orElseThrow();
                itemTotalPrice = cartService.updateItemQuantity(user, bookId, quantity);
                newSubtotal = cartService.getTotalPrice(user);
                newTotalItems = cartService.getCartItemCount(user);
            } else {
                // KHÁCH VÃNG LAI
                itemTotalPrice = cartService.updateSessionItemQuantity(session, bookId, quantity);
                newSubtotal = cartService.getSessionTotalPrice(session);
                newTotalItems = cartService.getSessionCartItemCount(session);
            }

            response.put("success", true);
            response.put("itemTotalPrice", itemTotalPrice);
            response.put("subtotal", newSubtotal);
            response.put("totalItems", newTotalItems);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ✅ === HÀM MỚI: XÓA SẢN PHẨM ===
    @PostMapping("/remove/{bookId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @PathVariable("bookId") Long bookId,
            Authentication authentication,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        double newSubtotal;
        int newTotalItems;

        try {
            if (authentication != null && authentication.isAuthenticated()) {
                // USER ĐÃ ĐĂNG NHẬP
                User user = userService.findByEmail(authentication.getName()).orElseThrow();
                cartService.removeItem(user, bookId);
                newSubtotal = cartService.getTotalPrice(user);
                newTotalItems = cartService.getCartItemCount(user);
            } else {
                // KHÁCH VÃNG LAI
                cartService.removeSessionItem(session, bookId);
                newSubtotal = cartService.getSessionTotalPrice(session);
                newTotalItems = cartService.getSessionCartItemCount(session);
            }

            response.put("success", true);
            response.put("subtotal", newSubtotal);
            response.put("totalItems", newTotalItems);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}