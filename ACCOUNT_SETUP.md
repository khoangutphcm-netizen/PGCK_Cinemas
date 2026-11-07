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

Chưa có xác minh email bắt buộc. Hồ sơ, lịch sử và xem tiếp đã có trong bản hiện tại.

## Nghiệp vụ người xem
- Publish lại firestore.rules: thêm users/{uid}/watchlist và users/{uid}/history. Dữ liệu chỉ chủ tài khoản đọc/ghi được.
- Tài khoản > Thư viện của tôi: Phim đã lưu / Tiếp tục xem / Lịch sử; nhấn giữ phim đã lưu để bỏ lưu. Không có nút xóa lịch sử.
- Chi tiết phim: bấm Danh sách để lưu/bỏ lưu; nút xem đổi sang Tiếp tục tập khi có tiến độ chưa hoàn thành.
- Player: lưu tiến độ mỗi 10 giây và khi rời màn hình; xem >=90% đánh dấu hoàn thành; tiếp tục hoặc xem lại; tập trước/sau; hết tập đếm ngược 5 giây, có Hủy.
- Trailer không ghi lịch sử. Nguồn MP4/HLS hỗ trợ đầy đủ; YouTube/HTML video lấy tiến độ qua API web khi nguồn cho phép. Iframe bên thứ ba không cung cấp API thì không thể đọc tiến độ.
- Hồ sơ: chỉnh tên và URL ảnh HTTPS trong thư viện, lưu vào Firebase Authentication. Chưa có upload ảnh từ thư viện máy.

Kiểm tra trên thiết bị với hai tài khoản: lưu phim ở A không xuất hiện ở B; tiến độ tập 1 không ghi đè tập 2; tắt/mở app rồi tiếp tục; hủy chuyển tập; mất mạng; từ chối đọc/ghi thư viện người khác trong Rules Playground.

## Dashboard thống kê

1. Publish toàn bộ `firestore.rules` mới, gồm `reactionEvents`. App ghi trạng thái đánh giá và sự kiện cùng một transaction; nếu thiếu quyền ghi nhật ký thì toàn bộ thay đổi đánh giá bị từ chối, không lưu một nửa.
2. Admin mở Tài khoản > Mở quản trị > Thống kê > Mở bảng thống kê.
3. Chọn tháng/năm hoặc toàn bộ; chọn năm từ 2000 đến 2100 và thể loại. Báo cáo dùng múi giờ UTC+7, khoảng bắt đầu bao gồm và khoảng kết thúc không bao gồm.
4. Sáu thẻ hiển thị view, người xem riêng biệt, like mới, dislike mới trong kỳ, số phim theo thể loại hiện tại và số hồ sơ toàn ứng dụng. Dòng đánh giá đang có là trạng thái hiện tại không giới hạn theo kỳ.
5. Biểu đồ theo ngày/tháng có bảng số liệu đọc được. Phân bố thể loại có số phim/view/like mới/dislike mới, bao gồm danh mục 0 phim. Bấm thể loại để lọc nhanh.
6. Xếp hạng cao/thấp theo view, like/dislike mới hoặc like/dislike đang có. Phim 0 tương tác vẫn được tính; phim đã gỡ có nhãn riêng.

Nhật ký `reactionEvents` chỉ bắt đầu từ bản cập nhật này. Không thể tái dựng lịch sử like/dislike trước đó từ `reactions`. Chuyển like sang dislike ghi 1 dislike mới; bỏ đánh giá ghi `to=none`; bấm like lại sau khi bỏ ghi thêm 1 like mới. Đây là số hành động, không phải số người thích duy nhất hoặc số thích ròng cuối kỳ.

Thể loại dùng danh mục hiện tại; với phim đã gỡ, sự kiện mới có lưu ảnh chụp tên và thể loại. Phim nhiều thể loại được tính vào từng nhóm nên tổng các nhóm có thể lớn hơn tổng toàn bộ. View vẫn theo quy tắc đăng nhập xem khoảng 30 giây, 1 lượt/phim/ngày UTC; bước này không thay đổi phần tải/offline.

Kiểm tra Firebase thật sau Publish:
- User xem tập đủ 30 giây, mở lại cùng ngày: chỉ 1 view; trailer không tạo view.
- User like > dislike > bỏ đánh giá: trạng thái cuối không còn reaction, nhật ký có 3 sự kiện (like/dislike/none); báo cáo kỳ có 1 like mới, 1 dislike mới, 1 bỏ, đang có = 0.
- Chọn kỳ rỗng: view/đánh giá mới = 0; số phim và trạng thái hiện tại vẫn có thể lớn hơn 0.
- Admin xem được `reactionEvents`; user không được sửa/xóa sự kiện hoặc ghi trạng thái cho tài khoản khác. Kiểm tra transaction hợp lệ trong Rules Playground/Emulator nếu cần đánh giá quy tắc.

Các phép tính được kiểm thử trong `StatsReportTest`: biên tháng UTC+7, năm nhuận, người xem duy nhất, nhiều thể loại, danh mục rỗng, lọc đồng nhất, xếp hạng có số 0, phim đã gỡ và tách nhật ký khỏi trạng thái hiện tại. Chưa thay thế kiểm tra Firebase thật. Với lượng dữ liệu lớn, cần chuyển tổng hợp sang máy chủ thay vì đọc toàn bộ trạng thái reaction về app.
