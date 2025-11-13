package com.devbooks.config;

import com.devbooks.cart.CartItem; // Đảm bảo import đúng
import com.devbooks.cart.CartService; // Đảm bảo import đúng
import com.devbooks.entity.User;
import com.devbooks.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();

        // 1. Lấy giỏ hàng tạm (Session) của khách
        Map<Long, Integer> sessionCart = cartService.getSessionCart(session);

        // 2. Kiểm tra và Gộp giỏ hàng
        if (sessionCart != null && !sessionCart.isEmpty()) {

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy user sau khi đăng nhập."));

            // Lặp qua giỏ hàng tạm và thêm vào giỏ hàng vĩnh viễn (DB)
            for (Map.Entry<Long, Integer> entry : sessionCart.entrySet()) {
                cartService.addBookToCart(user, entry.getKey(), entry.getValue());
            }

            // 3. Xóa giỏ hàng tạm (Session)
            session.removeAttribute("cart");
        }

        // 4. Chuyển hướng thông minh (Về trang chủ hoặc trang checkout)
        super.setDefaultTargetUrl("/"); // Trang mặc định nếu không có gì
        super.onAuthenticationSuccess(request, response, authentication);
    }
}