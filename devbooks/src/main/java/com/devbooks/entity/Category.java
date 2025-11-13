package com.devbooks.entity;

import jakarta.persistence.*;
// import lombok.Data; // ❌ Đã xóa
import lombok.Getter;   // ✅ Đã thêm
import lombok.Setter;   // ✅ Đã thêm
import java.util.List;

// @Data // ❌ Đã xóa
@Getter   // ✅ Đã thêm
@Setter   // ✅ Đã thêm
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Book> books;

    // ✅ Đã thêm: Tùy chỉnh hàm toString() để tránh lặp vô hạn
    // Chỉ in các trường cơ bản, KHÔNG in "books"
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}