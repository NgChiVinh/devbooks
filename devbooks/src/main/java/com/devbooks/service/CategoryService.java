package com.devbooks.service;

import com.devbooks.entity.Category;
import com.devbooks.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Tạo một danh mục mới
    public Category createCategory(Category category) {
        // Sau này có thể thêm logic kiểm tra tên danh mục có trùng không...
        return categoryRepository.save(category);
    }
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Kiểm tra xem danh mục có sách nào không
        if (category.getBooks() != null && !category.getBooks().isEmpty()) {
            // Nếu có, không cho xóa và báo lỗi
            throw new RuntimeException("Không thể xóa danh mục này vì vẫn còn sách.");
        }

        // Nếu không có sách nào, tiến hành xóa
        categoryRepository.deleteById(id);
    }

    // (Bạn có thể thêm các phương thức khác như updateCategory, deleteCategory...)
}