package com.devbooks.controller;

import com.devbooks.entity.User;
import com.devbooks.cart.CartService; // ✅ Sửa Import
import com.devbooks.service.UserService;
import jakarta.servlet.http.HttpSession; // ✅ Thêm Import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    /**
     * Tự động thêm "cartItemCount" vào Model cho MỌI TRANG
     */
    @ModelAttribute("cartItemCount")
    public int getCartItemCount(HttpSession session) { // ✅ Thêm HttpSession
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            // === KỊCH BẢN 1: USER ĐÃ ĐĂNG NHẬP ===
            try {
                String email = authentication.getName();
                User user = userService.findByEmail(email).orElse(null);
                if (user != null) {
                    return cartService.getCartItemCount(user); // Lấy từ DB
                }
            } catch (Exception e) {
                return 0;
            }
        }

        // === KỊCH BẢN 2: KHÁCH VÃNG LAI ===
        return cartService.getSessionCartItemCount(session); // Lấy từ Session
    }
}