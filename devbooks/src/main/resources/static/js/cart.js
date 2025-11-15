/**
 * Hàm Hiển Thị Thông Báo Toast (Tự tạo container)
 */
function showToast(message, type = 'info', duration = 3000) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerText = message;
    container.appendChild(toast);
    setTimeout(() => { toast.classList.add('show'); }, 10);
    setTimeout(() => {
        toast.classList.remove('show');
        toast.addEventListener('transitionend', () => {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
        });
    }, duration);
}

/**
 * ✅ HÀM MỚI: TỰ TẠO VÀ HIỂN THỊ MODAL XÁC NHẬN
 */
function showCustomConfirm(message, callback) {
    // 1. Tạo Lớp Phủ (Overlay)
    const modalOverlay = document.createElement('div');
    modalOverlay.className = 'modal-overlay';

    // 2. Tạo Nội dung (Content Box)
    const modalContent = document.createElement('div');
    modalContent.className = 'modal-content';
    modalContent.innerHTML = `
        <h3>Xác nhận</h3>
        <p>${message}</p>
        <div class="modal-actions">
            <button class="btn-modal btn-cancel">Hủy Bỏ</button>
            <button class="btn-modal btn-confirm">Đồng Ý</button>
        </div>
    `;

    modalOverlay.appendChild(modalContent);
    document.body.appendChild(modalOverlay);

    // 3. Hiệu ứng (Show)
    setTimeout(() => modalOverlay.classList.add('show'), 10);

    // 4. Lắng nghe các nút
    const btnConfirm = modalContent.querySelector('.btn-confirm');
    const btnCancel = modalContent.querySelector('.btn-cancel');

    // Hàm để đóng và xóa Modal
    const closeModal = () => {
        modalOverlay.classList.remove('show');
        modalOverlay.addEventListener('transitionend', () => {
            if (document.body.contains(modalOverlay)) {
                document.body.removeChild(modalOverlay);
            }
        });
    };

    // 5. Xử lý sự kiện
    btnConfirm.addEventListener('click', () => {
        callback(true); // Gọi hàm callback (đồng ý)
        closeModal();
    });
    btnCancel.addEventListener('click', () => {
        callback(false); // Gọi hàm callback (hủy)
        closeModal();
    });
}


// === BẮT ĐẦU CODE CHÍNH ===
document.addEventListener("DOMContentLoaded", function() {

    // === 1. XỬ LÝ THÊM VÀO GIỎ HÀNG (Giữ nguyên) ===
    const allAddForms = document.querySelectorAll('.add-to-cart-form');
    allAddForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            event.preventDefault();
            const url = form.action;

            fetch(url, {
                method: 'POST',
                headers: {}
            })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 400) {
                        return response.json().then(err => {
                            throw new Error(err.message || 'Lỗi không xác định');
                        });
                    }
                    throw new Error('Lỗi máy chủ: ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                if (data.success && data.totalItems !== undefined) {
                    const cartCountElement = document.getElementById('cart-item-count');
                    if (cartCountElement) {
                         cartCountElement.innerText = data.totalItems;
                    }
                    showToast('Đã thêm vào giỏ hàng!', 'success');
                } else {
                     console.error('Phản hồi từ server không thành công:', data.message);
                }
            })
            .catch(error => {
                showToast(error.message, 'error');
                console.error('Lỗi Fetch:', error);
            });
        });
    });

    // === 2. XỬ LÝ TRANG GIỎ HÀNG (Cập nhật số lượng & Xóa) ===
    const cartItemsList = document.querySelector('.cart-items-list');

    if (cartItemsList) {

        cartItemsList.addEventListener('click', function(event) {
            const target = event.target;
            let bookId = null;

            // --- Xử lý nút TĂNG/GIẢM (Giữ nguyên) ---
            if (target.classList.contains('quantity-btn')) {
                bookId = target.dataset.bookId;
                const quantityInput = cartItemsList.querySelector(`.quantity-input[data-book-id="${bookId}"]`);
                let currentQuantity = parseInt(quantityInput.value);
                let newQuantity = 0;

                if (target.classList.contains('increase-quantity-btn')) {
                    newQuantity = currentQuantity + 1;
                } else if (target.classList.contains('decrease-quantity-btn')) {
                    newQuantity = currentQuantity - 1;
                }

                if (newQuantity > 0) {
                    updateCartItemOnServer(bookId, newQuantity, quantityInput);
                } else {
                    // Nếu giảm về 0, hỏi xóa
                    showCustomConfirm('Bạn có chắc muốn xóa sản phẩm này?', (result) => {
                        if (result) { // Nếu nhấn "Đồng Ý"
                            removeCartItemOnServer(bookId);
                        }
                    });
                }
            }

            // --- ✅ SỬA LỖI: Xử lý nút XÓA (icon thùng rác) ---
            if (target.closest('.remove-item-btn')) {
                const button = target.closest('.remove-item-btn');
                bookId = button.dataset.bookId;

                // Thay thế confirm() bằng Modal mới
                showCustomConfirm('Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?', (result) => {
                    if (result) { // Nếu nhấn "Đồng Ý"
                        removeCartItemOnServer(bookId);
                    }
                    // Nếu nhấn "Hủy Bỏ", không làm gì cả
                });
            }
        });
    }

    // --- 3. (Các hàm logic (Update/Remove/Helpers) giữ nguyên) ---

    function updateCartItemOnServer(bookId, quantity, quantityInput) {
        fetch('/cart/update', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `bookId=${bookId}&quantity=${quantity}`
        })
        .then(response => {
            if (!response.ok) {
                if (response.status === 400) {
                    return response.json().then(err => {
                        throw new Error(err.message || 'Lỗi không xác định');
                    });
                }
                throw new Error('Lỗi máy chủ: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                if (quantityInput) {
                    quantityInput.value = quantity;
                }
                const itemCard = document.querySelector(`.cart-item-card[data-book-id="${bookId}"]`);
                if (itemCard) {
                    const itemTotalPriceElement = itemCard.querySelector('.item-total-price');
                    itemTotalPriceElement.innerText = formatCurrency(data.itemTotalPrice);
                }
                updateCartSummary(data.subtotal);
                updateHeaderCartCount(data.totalItems);
                showToast('Cập nhật số lượng thành công!', 'info');
            }
        })
        .catch(error => {
            showToast(error.message, 'error');
            console.error('Lỗi Fetch (Update):', error);
            setTimeout(() => { window.location.reload(); }, 1500);
        });
    }

    function removeCartItemOnServer(bookId) {
        fetch(`/cart/remove/${bookId}`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const itemCard = document.querySelector(`.cart-item-card[data-book-id="${bookId}"]`);
                if (itemCard) {
                    itemCard.remove();
                }
                updateCartSummary(data.subtotal);
                updateHeaderCartCount(data.totalItems);
                showToast('Đã xóa sản phẩm.', 'success');
                if (data.totalItems === 0) {
                    setTimeout(() => { window.location.reload(); }, 1000);
                }
            } else {
                showToast(data.message, 'error');
            }
        })
        .catch(error => {
             showToast('Lỗi khi xóa sản phẩm.', 'error');
             console.error('Lỗi Fetch (Remove):', error)
        });
    }

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
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' })
                       .format(amount)
                       .replace(' ₫', ' VNĐ');
    }
});