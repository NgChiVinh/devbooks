package com.devbooks.controller;

import com.devbooks.entity.Book;
import com.devbooks.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private BookService bookService;

    // Khi người dùng truy cập vào đường dẫn "/", phương thức này sẽ được gọi
    @GetMapping("/")
    public String home(Model model) {
        // 1. Gọi BookService để lấy tất cả sách
        List<Book> bookList = bookService.getAllBooks();

        // 2. "Gói" danh sách sách này vào một đối tượng Model với tên là "books"
        model.addAttribute("books", bookList);

        // 3. Trả về file "index.html" để hiển thị
        return "index";
    }
}