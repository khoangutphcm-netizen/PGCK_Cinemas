# Thiết lập tài khoản Firebase

Project: videoapp-7b327

1. Firebase Console > Authentication > Sign-in method: bật Email/Password.
2. Authentication > Users > Add user: tạo admin@gmail.com và user@gmail.com, tự đặt mật khẩu.
3. Firestore > Rules: xem xét và Publish nội dung firestore.rules trong dự án. Quy tắc này chỉ cho phép users, videos và genres; nếu dự án có collection khác cần hợp nhất trước khi Publish.
4. Sao chép UID của admin từ Authentication. Firestore > users > tạo document với ID chính xác là UID đó:
   - email (string): admin@gmail.com
   - role (string): admin
   - createdAt (timestamp): thời gian hiện tại
5. Tương tự tạo users/{UID_USER}, email user@gmail.com, role user, createdAt timestamp. Nếu chưa tạo, app tự tạo hồ sơ user khi đăng nhập thành công và có mạng.
6. Mở app, đăng nhập. Nút tài khoản góc trên mở menu. Admin có Mở quản trị; user chỉ có Đăng xuất.

Không cấp admin theo địa chỉ email trong mã nguồn. Không lưu mật khẩu trong Firestore hoặc ứng dụng. Hồ sơ chỉ được nâng quyền bằng Firebase Console hoặc Admin SDK đáng tin cậy.

## Kiểm tra thủ công
- Sai mật khẩu: hiện lỗi và cho thử lại.
- Đăng ký mới: đăng nhập được, hồ sơ role user, không có quản trị.
- Admin: mở quản trị, thêm/sửa/xóa phim.
- User: không có quản trị; Firestore Rules Playground phải từ chối ghi videos/genres và tự đổi role.
- Đăng xuất: quay về đăng nhập, Back không mở lại trang chủ.
- Mở lại ứng dụng: giữ phiên đăng nhập.
- Mất mạng khi kiểm tra quyền: khóa quản trị.
- Quên mật khẩu: thử bằng email thật do bạn sở hữu để nhận thư.

Chưa triển khai quản lý hồ sơ, xác minh email hoặc lịch sử xem trong bước này.

## Nghiệp vụ người xem
- Publish lại firestore.rules: thêm users/{uid}/watchlist và users/{uid}/history. Dữ liệu chỉ chủ tài khoản đọc/ghi được.
- Tài khoản > Thư viện của tôi: Phim đã lưu / Tiếp tục xem / Lịch sử; có bỏ lưu, xóa từng lịch sử và xóa tất cả.
- Chi tiết phim: bấm Danh sách để lưu/bỏ lưu; nút xem đổi sang Tiếp tục tập khi có tiến độ chưa hoàn thành.
- Player: lưu tiến độ mỗi 10 giây và khi rời màn hình; xem >=90% đánh dấu hoàn thành; tiếp tục hoặc xem lại; tập trước/sau; hết tập đếm ngược 5 giây, có Hủy.
- Trailer không ghi lịch sử. Nguồn MP4/HLS hỗ trợ đầy đủ; YouTube/HTML video lấy tiến độ qua API web khi nguồn cho phép. Iframe bên thứ ba không cung cấp API thì không thể đọc tiến độ.
- Hồ sơ: chỉnh tên và URL ảnh HTTPS trong thư viện, lưu vào Firebase Authentication. Chưa có upload ảnh từ thư viện máy.

Kiểm tra trên thiết bị với hai tài khoản: lưu phim ở A không xuất hiện ở B; tiến độ tập 1 không ghi đè tập 2; tắt/mở app rồi tiếp tục; hủy chuyển tập; xóa lịch sử; mất mạng; từ chối đọc/ghi thư viện người khác trong Rules Playground.
