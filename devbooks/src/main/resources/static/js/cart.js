// Chờ cho toàn bộ trang tải xong
document.addEventListener("DOMContentLoaded", function() {

    // 1. Tìm tất cả các form "Thêm vào giỏ"
    const allForms = document.querySelectorAll('.add-to-cart-form');

    // 2. Thêm sự kiện "submit" cho mỗi form
    allForms.forEach(form => {
        form.addEventListener('submit', function(event) {

            // 3. Ngăn chặn hành vi gửi form (tải lại trang)
            event.preventDefault();

            const url = form.action; // Lấy URL API (ví dụ: /api/cart/add/1)

            // 4. Gửi yêu cầu POST ngầm (Fetch API)
            fetch(url, {
                method: 'POST',
                headers: {
                    // (SecurityConfig của bạn đã tắt CSRF)
                }
            })
            .then(response => {
                // ✅ SỬA LỖI: Xóa bỏ kiểm tra 401
                // (Vì server sẽ luôn trả về 200 OK cho cả khách và user)

                if (!response.ok) {
                    // Nếu server trả về lỗi (ví dụ: 500), ném ra lỗi
                    throw new Error('Lỗi khi thêm vào giỏ hàng');
                }
                return response.json(); // Chuyển đổi kết quả (Map) thành JSON
            })
            .then(data => {
                // 5. Cập nhật số lượng trên icon giỏ hàng
                if (data.success && data.totalItems !== undefined) {
                    const cartCountElement = document.getElementById('cart-item-count');
                    cartCountElement.innerText = data.totalItems;

                    // (Tùy chọn: Hiển thị thông báo "Đã thêm thành công")
                } else {
                     console.error('Phản hồi từ server không thành công:', data.message);
                }
            })
            .catch(error => {
                console.error('Lỗi Fetch:', error);
            });
        });
    });
});