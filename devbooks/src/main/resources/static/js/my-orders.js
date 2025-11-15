/**
 * HÀM TỰ TẠO VÀ HIỂN THỊ MODAL XÁC NHẬN
 * (Copy từ cart.js)
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


// === BẮT ĐẦU CODE CHÍNH CỦA TRANG "MY ORDERS" ===
document.addEventListener("DOMContentLoaded", function() {

    const cancelForms = document.querySelectorAll('.cancel-form');

    cancelForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            // 1. Ngăn form tự động gửi đi
            event.preventDefault();

            // 2. Gọi Modal xác nhận
            showCustomConfirm('Bạn có chắc muốn hủy đơn hàng này? Thao tác này không thể hoàn tác.', (result) => {

                // 3. Nếu user nhấn "Đồng Ý" (result == true)
                if (result) {
                    // 4. Mới thực sự submit form
                    form.submit();
                }
                // Nếu nhấn "Hủy Bỏ", không làm gì cả
            });
        });
    });
});