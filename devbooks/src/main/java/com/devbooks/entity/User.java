package com.devbooks.entity;

import jakarta.persistence.*;
// import lombok.Data; // ❌ XÓA
import lombok.Getter;   // ✅ THÊM
import lombok.Setter;   // ✅ THÊM
import java.util.List;

// @Data // ❌ XÓA
@Getter   // ✅ THÊM
@Setter   // ✅ THÊM
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phoneNumber;

    private String address;

    @Column(nullable = false, length = 20)
    private String role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    // ✅ THÊM: Tự viết toString() để phá vỡ vòng lặp
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
        // Chỉ in các trường an toàn, KHÔNG in "orders"
    }
}