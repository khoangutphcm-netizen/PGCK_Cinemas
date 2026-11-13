package com.example.pgck2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.*;

public class WelcomeActivity extends AppCompatActivity {
    private int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
    private TextView text(String value,int size,int color){TextView text=new TextView(this);text.setText(value);text.setTextSize(size);text.setTextColor(color);text.setLineSpacing(dp(3),1);return text;}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(24),dp(28),dp(24),dp(24));root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff31121c,0xff0e0f16,0xff171a28}));scroll.addView(root);
        TextView brand=text("PCGK Cinema",23,0xffff4355);brand.setTypeface(null,Typeface.BOLD);root.addView(brand);
        TextView eyebrow=text("RẠP PHIM TRONG TẦM TAY",11,0xffd4adba);eyebrow.setLetterSpacing(.14f);eyebrow.setPadding(0,dp(40),0,dp(14));root.addView(eyebrow);
        TextView headline=text("Một bộ phim hay.\nMột thế giới mới.",34,-1);headline.setTypeface(null,Typeface.BOLD);root.addView(headline);
        TextView subtitle=text("Khám phá nội dung yêu thích, lưu để xem sau và tiếp tục câu chuyện theo cách của bạn.",15,0xffc2bbc8);subtitle.setPadding(0,dp(18),0,dp(28));root.addView(subtitle);
        LinearLayout features=new LinearLayout(this);root.addView(features);
        String[] names={"Xem tiếp","Tủ phim","Premium"};int[] colors={0xff7dbbff,0xff54dbc0,0xffffc674};int[] backgrounds={0xff1d293d,0xff16302d,0xff352735};
        for(int i=0;i<3;i++){MaterialCardView card=new MaterialCardView(this);card.setRadius(dp(16));card.setCardBackgroundColor(backgrounds[i]);card.setStrokeWidth(dp(1));card.setStrokeColor(colors[i]);TextView label=text(names[i],13,colors[i]);label.setGravity(Gravity.CENTER);label.setPadding(dp(6),dp(24),dp(6),dp(24));card.addView(label);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);if(i<2)p.rightMargin=dp(10);features.addView(card,p);}
        TextView greeting=text(user==null?"Bạn có thể khám phá phim miễn phí mà chưa cần tài khoản.":"Chào mừng trở lại, "+(user.getDisplayName()==null?user.getEmail():user.getDisplayName()),14,0xffb6b4c8);greeting.setPadding(0,dp(30),0,dp(14));root.addView(greeting);
        MaterialButton start=new MaterialButton(this);start.setText(user==null?"Khám phá ngay":"Tiếp tục với tài khoản của bạn");start.setAllCaps(false);start.setCornerRadius(dp(16));start.setTextColor(-1);start.setBackgroundTintList(ColorStateList.valueOf(0xffdf1730));root.addView(start,new LinearLayout.LayoutParams(-1,dp(58)));start.setOnClickListener(v->{startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();});
        if(user==null){MaterialButton login=new MaterialButton(this);login.setText("Đăng nhập");login.setAllCaps(false);login.setCornerRadius(dp(16));login.setTextColor(0xfff4e9ed);login.setBackgroundTintList(ColorStateList.valueOf(0xff302331));login.setStrokeWidth(dp(1));login.setStrokeColor(ColorStateList.valueOf(0xff6a3849));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(58));p.topMargin=dp(10);root.addView(login,p);login.setOnClickListener(v->startActivity(new Intent(this,LoginActivity.class)));link(root,"Chưa có tài khoản?  Đăng ký",()->startActivity(new Intent(this,SignUpActivity.class)));}
        else link(root,"Đăng xuất / Đổi tài khoản",()->SessionNavigation.confirmLogout(this));
        TextView credits=text(getString(R.string.student_one)+"\n"+getString(R.string.student_two),11,0xff9c909f);credits.setPadding(0,dp(30),0,0);root.addView(credits);setContentView(scroll);CinemaStyle.applyInsets(scroll);
    }
    private void link(LinearLayout parent,String label,Runnable action){TextView text=text(label,14,0xffff8290);text.setGravity(Gravity.CENTER);text.setPadding(0,dp(18),0,dp(18));parent.addView(text);text.setOnClickListener(v->action.run());}
}
