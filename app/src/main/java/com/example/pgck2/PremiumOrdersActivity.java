package com.example.pgck2;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;
public class PremiumOrdersActivity extends AppCompatActivity {
 @Override public void onCreate(Bundle state){super.onCreate(state);LinearLayout body=new LinearLayout(this);body.setOrientation(1);body.setPadding(32,40,32,24);ScrollView scroll=new ScrollView(this);scroll.addView(body);setContentView(scroll);TextView title=new TextView(this);title.setText("Đơn Premium · MÔ PHỎNG");title.setTextSize(23);body.addView(title);Button back=new Button(this);back.setText("‹ Panel");body.addView(back);back.setOnClickListener(v->finish());TextView result=new TextView(this);body.addView(result);CinemaStyle.apply(this);result.setText("Đang tải…");
  if(FirebaseAuth.getInstance().getCurrentUser()==null){finish();return;}
  FirebaseFirestore db=FirebaseFirestore.getInstance();db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get(Source.SERVER).addOnSuccessListener(user->{if(!"admin".equals(user.getString("role"))){finish();return;}db.collection("premiumOrders").orderBy("createdAt",Query.Direction.DESCENDING).get(Source.SERVER).addOnSuccessListener(orders->{StringBuilder text=new StringBuilder("Không phải doanh thu thật.\n\n");long total=0;int confirmed=0;for(DocumentSnapshot doc:orders){boolean paid="confirmed".equals(doc.getString("status"));if(paid){confirmed++;total+=doc.getLong("amount");}text.append(doc.getId()).append("\nUID: ").append(doc.getString("uid")).append("\n").append(doc.getString("plan")).append(" · ").append(doc.getLong("amount")).append("đ · ").append(paid?"Đã xác nhận mô phỏng":"Chờ xác nhận").append("\n\n");}result.setText("Tổng đơn: "+orders.size()+" · Đã xác nhận: "+confirmed+"\nGiá trị mô phỏng: "+total+"đ\n\n"+text);}).addOnFailureListener(e->result.setText("Chưa tải được đơn. Kiểm tra Rules và kết nối."));}).addOnFailureListener(e->result.setText("Chưa xác minh được quyền admin."));
 }
}
