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

## Premium mô phỏng và PiP

- Publish Rules mới cho `premiumOrders` và `subscriptions` trước khi chạy. Đây là checkout mô phỏng có chủ ý cho phép chủ đơn tự xác nhận, không phải xác thực thanh toán ngân hàng.
- User vào Tài khoản > Premium. Gói tháng là 30 ngày/49.000đ mô phỏng; gói năm là 365 ngày/399.000đ mô phỏng. Có thể đổi giá/ngày trong PremiumPlans và điều kiện tương ứng của Rules.
- Mã QR thật chứa `pcgkcinema://premium/order/{id}`. Camera đọc được liên kết; thiết bị đã cài app và đăng nhập đúng chủ đơn có thể mở đơn qua liên kết. QR không chuyển tiền và không tự đánh dấu đã thanh toán.
- Xác nhận mô phỏng ghi trạng thái đơn và liên kết gói cùng transaction. Thời hạn tính từ confirmedAt do Firebase cấp. Xác nhận lại cùng đơn không cộng thêm ngày. Không bán thêm gói khi gói hiện tại còn hạn; chưa có gia hạn cộng dồn hoặc tự động thu tiền.
- Admin chỉnh phim, bật Video Premium và lưu. User mở tập: Player kiểm tra phim từ server và gói trước khi phát; admin có thể xem để kiểm tra nội dung. Trailer vẫn mở tự do. Panel > Thống kê > Đơn Premium xem các đơn mô phỏng.
- Đây là kiểm soát truy cập ở ứng dụng demo; URL media hiện vẫn nằm trong catalog. Bản thương mại cần backend xác minh thanh toán, thời gian/quyền truy cập và URL ký có hạn; không dùng Rules tự xác nhận mô phỏng cho giao dịch thật.
- PiP: nhấn nút để thu nhỏ, nhấn giữ để chọn theo video/16:9/9:16 hoặc bật tắt tự PiP. Android 12+ dùng auto-enter khi đang phát; Android cũ dùng Home callback. Có lùi/phát/tới, ưu tiên phát/tạm dừng nếu thiết bị chỉ hỗ trợ ít nút. Nguồn web phụ thuộc API player. Android tự quyết định kích thước cửa sổ, không đặt kích thước chính xác bằng phần trăm màn hình.

Kiểm tra Firebase thật sau Publish: user chưa có gói bị chặn Premium; free và trailer vẫn xem được; xác nhận đơn month/year rồi mở lại tập Premium; thử xác nhận cùng đơn hai lần; tài khoản khác không đọc/ghi đơn hoặc gói của chủ đơn; kiểm tra hạn hết. Dữ liệu thanh toán không chứa mật khẩu hoặc số tài khoản ngân hàng.

## Thứ tự cải thiện tiếp theo

1. Tách editor/repository khỏi MainActivity (AdminPanel hiện kế thừa Main để dùng lại upload và form), thêm sửa/đổi tên genre và đảm bảo lỗi ghi không làm giao diện báo thành công.
2. Backend Admin SDK cho thêm/sửa/xóa/khóa tài khoản và thu hồi phiên; thống nhất hồ sơ Auth/Firestore. Cần quyền triển khai Firebase để hoàn tất phần này.
3. Kiểm tra xuyên suốt user/admin/Premium trên Firebase, tối ưu lịch sử chỉ ghi khi tiến độ đổi, tổng hợp thống kê phía server khi dữ liệu lớn. Bổ sung nhật ký thao tác quản trị và tìm kiếm không dấu.
4. Sau cùng mới triển khai tải và offline: Room, thumbnail, danh sách tải, tiến độ và nguồn phát local.

## Các thao tác bạn cần tự làm trên Firebase

Chọn đúng project ghi trong `app/google-services.json`.

1. Authentication > Sign-in method: bật Email/Password nếu chưa bật.
2. Authentication > Users: tạo tài khoản bằng email bạn sở hữu để nhận thư đặt lại mật khẩu. Tài khoản test admin@gmail.com/user@gmail.com chỉ dùng nếu bạn đã tự tạo và biết mật khẩu; tên email không tự cấp quyền admin.
3. Để cấp admin đầu tiên: sao chép UID từ Authentication. Firestore Database > Data > users > document ID đúng UID đó. Thêm email (string), role (string) = admin, createdAt (timestamp). User thường dùng role = user; app tạo hồ sơ user ở lần đăng nhập đầu nếu có quyền ghi.
4. Firestore Database > Rules: dán nội dung hiện tại của `firestore.rules` và Publish. Nếu đã Publish đúng bản mới nhất thì không cần làm lại cho thay đổi Welcome. File bao gồm users, videos, genres, reactions, reactionEvents, viewEvents, premiumOrders, subscriptions và thư viện cá nhân.
5. Không cần tự tạo từng document lịch sử, view, like, đơn hàng hoặc gói. App tạo khi có thao tác hợp lệ. Không tạo trạng thái thanh toán giả thủ công trong Console: dùng nút xác nhận mô phỏng trong app để lưu cả đơn và gói cùng transaction.
6. Muốn thử Premium: admin bật Video Premium trên phim có tập hợp lệ; đăng nhập user thường, mua gói mô phỏng rồi mở lại tập. Admin được xem nội dung để quản lý nên cần dùng user thường để thử chặn Premium.

Chưa cần bật thanh toán ngân hàng hoặc nhập thông tin thẻ cho QR mô phỏng. App không triển khai backend Admin SDK: việc thêm/xóa/khóa tài khoản Auth vẫn làm trong Firebase Console ở giai đoạn này. Panel chỉ đổi role Firestore và gửi email đặt lại mật khẩu.

## Welcome và kết quả rà UX

- Launcher mới là WelcomeActivity. Welcome cho xem khách/đăng nhập/đăng ký; có phiên đăng nhập thì hiện nút tiếp tục và đổi tài khoản. Android Studio dùng Launch: Default Activity, không chọn Specified Activity là demo hoặc LoginActivity.
- Đăng xuất có xác nhận, quay về Welcome và xóa back stack để không Back trở lại admin. Liên kết từ đăng ký/quên mật khẩu về đăng nhập là điều hướng thật, kể cả khi đăng ký được mở trực tiếp từ Welcome.
- Admin kiểm tra lại role khi quay về panel, hủy hiệu lực callback từ phiên cũ, có retry nếu mất mạng. Search lọc trên danh sách đã tải thay vì gọi Firebase mỗi ký tự. User/admin tìm kiếm hỗ trợ tiếng Việt không dấu.
- Bình luận CHƯA có: chưa có collection, UI, phân quyền hay kiểm duyệt comment. Đây là câu hỏi kiểm tra hiện trạng, chưa được mở rộng thành tính năng trong đợt Welcome.
- UX còn cần cải thiện: editor phim/tập vẫn nhiều trường và dựa vào MainActivity; chọn ảnh hồ sơ mới bằng URL; user/Auth và hồ sơ Firestore chưa đồng bộ đầy đủ; Premium là mô phỏng và chưa trả về tự động đúng tập sau khi mua. Tải/offline giữ ở cuối kế hoạch.
