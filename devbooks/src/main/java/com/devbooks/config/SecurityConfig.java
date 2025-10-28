package com.devbooks.config;

import com.devbooks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean này "dạy" Spring Security cách tìm và xác thực người dùng
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService); // Dùng UserService của bạn
        authProvider.setPasswordEncoder(passwordEncoder()); // Dùng PasswordEncoder của bạn
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Phân quyền
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Chỉ ADMIN được vào /admin

                        // THÊM "/login" VÀO ĐÂY
                        .requestMatchers("/", "/home", "/register", "/book/**", "/login").permitAll()

                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll() // Tài nguyên tĩnh
                        .anyRequest().authenticated() // Tất cả các trang khác phải đăng nhập
                )
                // 2. Cấu hình Form Login
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Đường dẫn đến trang login của bạn
                        .loginProcessingUrl("/login") // URL mà form sẽ POST đến để Spring Security xử lý
                        .defaultSuccessUrl("/", true) // Đăng nhập thành công thì về trang chủ
                        .permitAll() // Cho phép tất cả mọi người truy cập trang /login
                )
                // 3. Cấu hình Logout
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL để kích hoạt logout
                        .logoutSuccessUrl("/") // Logout thành công thì về trang chủ
                        .permitAll()
                );

        // 4. Áp dụng provider xác thực của bạn
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}