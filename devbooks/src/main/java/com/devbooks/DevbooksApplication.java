package com.devbooks;

import com.devbooks.entity.User;
import com.devbooks.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DevbooksApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevbooksApplication.class, args);
    }

    // Đảm bảo bạn có đoạn code này
    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Tạo tài khoản admin mẫu nếu chưa có
            if (userRepository.findByEmail("admin@devbooks.com").isEmpty()) {
                User admin = new User();
                admin.setFullName("Admin User");
                admin.setEmail("admin@devbooks.com");
                admin.setPassword(passwordEncoder.encode("admin123")); // Mật khẩu là admin123
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println(">>> Đã tạo tài khoản admin mẫu!");
            }
        };
    }
}