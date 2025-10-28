package com.devbooks.controller;

import com.devbooks.entity.User;
import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Hiển thị trang đăng nhập tùy chỉnh
     * URL: GET /login
     */
    @GetMapping("/login")
    public String showLoginForm() {
        // Trả về file tại: /resources/templates/auth/login.html
        return "auth/login";
    }

    /**
     * Hiển thị trang đăng ký
     * URL: GET /register
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        // Trả về file tại: /resources/templates/auth/register.html
        return "auth/register";
    }

    /**
     * Xử lý dữ liệu từ form đăng ký
     * URL: POST /register
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.registerUser(user);
        return "redirect:/register?success";
    }
}