package com.devbooks.controller;

import com.devbooks.entity.User;
import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ✅ === THÊM CÁC IMPORT CHO AUTO-LOGIN ===
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.http.HttpServletRequest;
// ✅ === KẾT THÚC IMPORT ===

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ✅ 1. TIÊM (INJECT) AUTHENTICATION MANAGER
    // (Bean này đã được tạo sẵn trong SecurityConfig)
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Hiển thị trang Đăng nhập
     * GET /login
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    /**
     * Hiển thị trang Đăng ký
     * GET /register
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    /**
     * Xử lý Đăng ký (VÀ TỰ ĐỘNG ĐĂNG NHẬP)
     * POST /register
     */
    @PostMapping("/register")
    public String processRegistration(
            @ModelAttribute("user") User user,
            HttpServletRequest request, // ✅ Thêm HttpServletRequest
            RedirectAttributes redirectAttributes
    ) {
        try {
            // 1. Lấy mật khẩu thô (chưa mã hóa)
            String rawPassword = user.getPassword();

            // 2. Đăng ký user (lúc này mật khẩu đã bị mã hóa trong DB)
            userService.registerUser(user);

            // 3. TỰ ĐỘNG ĐĂNG NHẬP
            // Tạo một token (vé) xác thực
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(user.getEmail(), rawPassword);

            // Xác thực vé
            Authentication authentication = authenticationManager.authenticate(token);

            // Đặt (Set) phiên (Session) đăng nhập
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Lưu context vào HttpSession
            request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            // 4. Chuyển hướng đến trang chủ (thay vì /login)
            return "redirect:/";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
}