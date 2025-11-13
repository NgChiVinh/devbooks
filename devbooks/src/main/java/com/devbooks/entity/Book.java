package com.devbooks.entity;

import jakarta.persistence.*;
// import lombok.Data; // ❌ Đã xóa
import lombok.Getter;   // ✅ Đã thêm
import lombok.Setter;   // ✅ Đã thêm
import java.time.LocalDateTime;

// @Data // ❌ Đã xóa
@Getter   // ✅ Đã thêm
@Setter   // ✅ Đã thêm
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 100)
    private String author;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private int stockQuantity;

    private String coverImageUrl;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // ✅ Đã thêm: Tùy chỉnh hàm toString() để tránh lặp vô hạn
    // Chỉ in các trường cơ bản, KHÔNG in "category"
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                '}';
    }
}