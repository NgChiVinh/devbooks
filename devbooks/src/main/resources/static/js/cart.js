// Chờ cho toàn bộ trang tải xong
document.addEventListener("DOMContentLoaded", function() {

    // === 1. XỬ LÝ THÊM VÀO GIỎ HÀNG (Trang Index/Products/Detail) ===
    const allAddForms = document.querySelectorAll('.add-to-cart-form');
    allAddForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            event.preventDefault(); // Ngăn chặn tải lại trang
            const url = form.action;

            fetch(url, {
                method: 'POST',
                headers: {} // CSRF đã disable
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Lỗi khi thêm vào giỏ hàng: ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                if (data.success && data.totalItems !== undefined) {
                    // Cập nhật số lượng trên icon giỏ hàng ở header
                    const cartCountElement = document.getElementById('cart-item-count');
                    if (cartCountElement) {
                         cartCountElement.innerText = data.totalItems;
                    }
                    // Tùy chọn: Hiển thị thông báo (ví dụ: dùng thư viện SweetAlert)
                    // alert('Đã thêm sản phẩm vào giỏ hàng!');
                } else {
                     console.error('Phản hồi từ server không thành công:', data.message);
                }
            })
            .catch(error => {
                console.error('Lỗi Fetch (Thêm vào giỏ):', error);
            });
        });
    });

    // === 2. XỬ LÝ TRANG GIỎ HÀNG (Cập nhật số lượng & Xóa) ===
    const cartItemsList = document.querySelector('.cart-items-list');

    // Chỉ chạy code này nếu chúng ta đang ở trang giỏ hàng
    if (cartItemsList) {

        cartItemsList.addEventListener('click', function(event) {
            const target = event.target;
            let bookId = null;
            let newQuantity = 0;

            // --- Xử lý nút TĂNG/GIẢM số lượng ---
            if (target.classList.contains('quantity-btn')) {
                bookId = target.dataset.bookId;
                const quantityInput = cartItemsList.querySelector(`.quantity-input[data-book-id="${bookId}"]`);
                let currentQuantity = parseInt(quantityInput.value);

                if (target.classList.contains('increase-quantity-btn')) {
                    newQuantity = currentQuantity + 1;
                } else if (target.classList.contains('decrease-quantity-btn')) {
                    newQuantity = currentQuantity - 1;
                }

                // Gửi yêu cầu cập nhật (chỉ khi số lượng > 0)
                if (newQuantity > 0) {
                    updateCartItemOnServer(bookId, newQuantity, quantityInput);
                } else {
                    // Nếu giảm về 0, chạy logic xóa
                    removeCartItemOnServer(bookId);
                }
            }

            // --- Xử lý nút XÓA sản phẩm (icon thùng rác) ---
            if (target.closest('.remove-item-btn')) {
                const button = target.closest('.remove-item-btn');
                bookId = button.dataset.bookId;
                if (confirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?')) {
                    removeCartItemOnServer(bookId);
                }
            }
        });
    }

    // --- Hàm gửi yêu cầu CẬP NHẬT (Update) đến server ---
    function updateCartItemOnServer(bookId, quantity, quantityInput) {
        fetch('/cart/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded' // Gửi dữ liệu dạng form
            },
            body: `bookId=${bookId}&quantity=${quantity}` // Dữ liệu gửi đi
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Cập nhật số lượng trên input
                if (quantityInput) {
                    quantityInput.value = quantity;
                }

                // Cập nhật tổng tiền của item đó
                const itemCard = document.querySelector(`.cart-item-card[data-book-id="${bookId}"]`);
                if (itemCard) {
                    const itemTotalPriceElement = itemCard.querySelector('.item-total-price');
                    itemTotalPriceElement.innerText = formatCurrency(data.itemTotalPrice);
                }

                // Cập nhật tổng tiền (subtotal)
                updateCartSummary(data.subtotal);

                // Cập nhật icon header
                updateHeaderCartCount(data.totalItems);
            } else {
                alert('Lỗi cập nhật: ' + data.message);
            }
        })
        .catch(error => console.error('Lỗi Fetch (Update):', error));
    }

    // --- Hàm gửi yêu cầu XÓA (Remove) đến server ---
    function removeCartItemOnServer(bookId) {
        fetch(`/cart/remove/${bookId}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Xóa item khỏi giao diện
                const itemCard = document.querySelector(`.cart-item-card[data-book-id="${bookId}"]`);
                if (itemCard) {
                    itemCard.remove();
                }

                // Cập nhật tổng tiền (subtotal)
                updateCartSummary(data.subtotal);

                // Cập nhật icon header
                updateHeaderCartCount(data.totalItems);

                // Nếu giỏ hàng trống, tải lại trang để hiển thị "Giỏ hàng trống"
                if (data.totalItems === 0) {
                    window.location.reload();
                }
            } else {
                alert('Lỗi khi xóa: ' + data.message);
            }
        })
        .catch(error => console.error('Lỗi Fetch (Remove):', error));
    }

    // --- Hàm helper (hàm phụ trợ) ---
    function updateCartSummary(newSubtotal) {
        const subtotalElement = document.getElementById('cart-subtotal');
        const finalTotalElement = document.getElementById('cart-final-total');
        const formattedTotal = formatCurrency(newSubtotal);

        if (subtotalElement) subtotalElement.innerText = formattedTotal;
        if (finalTotalElement) finalTotalElement.innerText = formattedTotal;
    }

    function updateHeaderCartCount(newTotalItems) {
        const cartCountElement = document.getElementById('cart-item-count');
        if (cartCountElement) {
            cartCountElement.innerText = newTotalItems;
        }
    }

    // Hàm định dạng tiền tệ
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' })
                       .format(amount)
                       .replace(' ₫', ' VNĐ'); // Thay thế ký tự VND
    }
});