package com.example.pgck2;

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

final class SessionNavigation {
    static void welcome(Activity activity){activity.startActivity(new Intent(activity,WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));activity.finish();}
    static void confirmLogout(Activity activity){new MaterialAlertDialogBuilder(activity).setTitle("Đăng xuất tài khoản?").setMessage("Bạn vẫn có thể xem phim miễn phí với tư cách khách. Lịch sử và phim đã lưu vẫn thuộc tài khoản của bạn.").setNegativeButton("Ở lại",null).setPositiveButton("Đăng xuất",(dialog,which)->{FirebaseAuth.getInstance().signOut();welcome(activity);}).show();}
}
