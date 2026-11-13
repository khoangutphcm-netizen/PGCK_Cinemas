package com.example.pgck2;
import android.app.Activity;
import android.content.Intent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.android.gms.tasks.*;

final class PremiumAccess {
 static Task<DocumentSnapshot> membership(){String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();FirebaseFirestore db=FirebaseFirestore.getInstance();return db.collection("subscriptions").document(uid).get(Source.SERVER).continueWithTask(t->{if(!t.isSuccessful())throw t.getException();String order=t.getResult().getString("orderId");return order==null?Tasks.forResult(null):db.collection("premiumOrders").document(order).get(Source.SERVER);});}
 static boolean active(DocumentSnapshot order){return order!=null&&"confirmed".equals(order.getString("status"))&&order.getTimestamp("confirmedAt")!=null&&PremiumPlans.active(order.getTimestamp("confirmedAt").toDate().getTime(),order.getString("plan"),System.currentTimeMillis());}
 static void check(Activity activity,Video video,Runnable allowed){
  if(video==null){allowed.run();return;}
  FirebaseFirestore db=FirebaseFirestore.getInstance();
  db.collection("videos").document(video.getId()).get(Source.SERVER).addOnSuccessListener(doc->{if(activity.isFinishing())return;if(!doc.exists()){deny(activity,"Phim không còn trong danh mục.");return;}if(!Boolean.TRUE.equals(doc.getBoolean("premium"))){allowed.run();return;}
   if(FirebaseAuth.getInstance().getCurrentUser()==null){deny(activity,"Đăng nhập và kích hoạt Premium để xem phim này.");return;}
   db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get(Source.SERVER).addOnSuccessListener(profile->{if(activity.isFinishing())return;if("admin".equals(profile.getString("role"))){allowed.run();return;}membership().addOnSuccessListener(order->{if(activity.isFinishing())return;if(active(order))allowed.run();else deny(activity,"Video dành cho tài khoản Premium còn hạn.");}).addOnFailureListener(e->deny(activity,"Chưa xác minh được gói Premium. Kiểm tra kết nối."));}).addOnFailureListener(e->deny(activity,"Chưa xác minh được tài khoản."));
  }).addOnFailureListener(e->deny(activity,"Chưa tải được thông tin video. Kiểm tra kết nối."));
 }
 private static void deny(Activity activity,String message){if(activity.isFinishing())return;new MaterialAlertDialogBuilder(activity).setTitle("PCGK Cinema Premium").setMessage(message).setPositiveButton("Xem gói Premium",(d,w)->{activity.startActivity(new Intent(activity,PremiumActivity.class));activity.finish();}).setNegativeButton("Đóng",(d,w)->activity.finish()).setOnCancelListener(d->activity.finish()).show();}
}
