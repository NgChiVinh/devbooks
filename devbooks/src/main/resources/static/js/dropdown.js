// Chờ cho toàn bộ trang (DOM) tải xong
document.addEventListener("DOMContentLoaded", function() {

    // 1. Tìm nút bấm icon user và hộp dropdown
    const menuButton = document.getElementById("user-menu-button");
    const dropdown = document.getElementById("user-dropdown");

    // 2. Kiểm tra xem 2 thẻ này có tồn tại không
    if (menuButton && dropdown) {

        // 3. Thêm sự kiện Click vào nút icon
        menuButton.addEventListener("click", function(event) {
            // Ngăn chặn link <a> nhảy trang
            event.preventDefault();

            // Thêm/Xóa class 'show' để Bật/Tắt dropdown
            dropdown.classList.toggle("show");
        });

        // 4. (Nâng cao) Tự động đóng dropdown nếu click ra ngoài
        window.addEventListener("click", function(event) {
            // Nếu click KHÔNG PHẢI là nút icon VÀ KHÔNG PHẢI là bên trong dropdown
            if (!menuButton.contains(event.target) && !dropdown.contains(event.target)) {
                if (dropdown.classList.contains("show")) {
                    dropdown.classList.remove("show");
                }
            }
        });
    }
});