package com.devbooks.service;

import com.devbooks.entity.Book;
import com.devbooks.repository.BookRepository;
import com.devbooks.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

// ✅ === IMPORT MỚI CHO CLOUDINARY ===
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
// ✅ === KẾT THÚC IMPORT MỚI ===

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // ✅ === TIÊM (INJECT) CLOUDINARY BEAN ===
    @Autowired
    private Cloudinary cloudinary;
    // ✅ === KẾT THÚC TIÊM BEAN ===

    // Lấy tất cả sách
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Lấy thông tin một cuốn sách qua ID
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // Tìm sách theo danh mục
    public List<Book> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    // Thêm hoặc cập nhật một cuốn sách
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Xóa một cuốn sách
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Tìm kiếm sách theo tiêu đề
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }

    /**
     * Lấy 5 cuốn sách mới nhất (chức năng 7)
     */
    public List<Book> getNewestBooks() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        return bookRepository.findAll(pageable).getContent();
    }

    /**
     * Lấy 5 cuốn sách bán chạy nhất (chức năng 7)
     */
    public List<Book> getTopSellingBooks() {
        // Lấy 5 ID sách bán chạy nhất từ repository
        Pageable pageable = PageRequest.of(0, 5);
        List<Long> topBookIds = orderDetailRepository.findTopSellingBookIds(pageable);

        // Từ các ID, lấy thông tin sách đầy đủ
        return topBookIds.stream()
                .map(id -> bookRepository.findById(id)) // Tìm sách
                .filter(Optional::isPresent) // Lọc bỏ các sách không tìm thấy (nếu có)
                .map(Optional::get) // Lấy đối tượng Book từ Optional
                .collect(Collectors.toList()); // Thu thập lại thành List<Book>
    }

    // ✅ === BẮT ĐẦU HÀM UPLOAD ẢNH MỚI ===

    /**
     * Hàm này nhận một file (MultipartFile) từ AdminController,
     * upload nó lên Cloudinary, và trả về URL (String) của ảnh đã upload.
     */
    public String uploadCoverImage(MultipartFile file) {
        try {
            // Upload file lên Cloudinary
            // "devbooks_uploads" là tên thư mục bạn muốn tạo trên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "devbooks_uploads"));

            // Lấy URL an toàn (https) của ảnh đã upload
            String secureUrl = (String) uploadResult.get("secure_url");

            return secureUrl;

        } catch (IOException e) {
            // Xử lý lỗi (ví dụ: file rỗng, lỗi kết nối)
            e.printStackTrace();
            // Bạn có thể ném ra một exception tùy chỉnh ở đây nếu muốn
            throw new RuntimeException("Không thể upload ảnh: " + e.getMessage());
        }
    }
    // ✅ === KẾT THÚC HÀM UPLOAD ẢNH MỚI ===
}