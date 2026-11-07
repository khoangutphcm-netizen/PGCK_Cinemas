package com.example.pgck2;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.textfield.*;

final class AuthScreen {
 static void show(AppCompatActivity a,int mode){
  FirebaseAuth auth=FirebaseAuth.getInstance();
  Runnable home=()->{a.startActivity(new Intent(a,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));a.finish();};
  if(auth.getCurrentUser()!=null){home.run();return;}
  float d=a.getResources().getDisplayMetrics().density;
  ScrollView scroll=new ScrollView(a);scroll.setFillViewport(true);
  LinearLayout root=new LinearLayout(a);root.setOrientation(1);root.setPadding((int)(28*d),(int)(32*d),(int)(28*d),(int)(24*d));scroll.addView(root);
  root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{0xff170607,0xff090909,0xff23080b}));
  label(root,"◉  CINEMA",16,0xfff04752);
  TextView hero=label(root,mode==0?"Câu chuyện hay.\nBắt đầu từ đây.":mode==1?"Một tài khoản.\nCả thế giới phim.":"Tìm lại lối vào\nthế giới của bạn.",32,0xffffffff);hero.setPadding(0,(int)(36*d),0,(int)(28*d));hero.setTypeface(null,1);
  LinearLayout card=new LinearLayout(a);card.setOrientation(1);card.setPadding((int)(20*d),(int)(24*d),(int)(20*d),(int)(20*d));GradientDrawable bg=new GradientDrawable();bg.setColor(0xff191315);bg.setCornerRadius(24*d);bg.setStroke((int)d,0xff5b272c);card.setBackground(bg);root.addView(card);
  label(card,mode==0?"Chào mừng trở lại":mode==1?"Tạo tài khoản":"Quên mật khẩu?",24,0xfffaf5ee);
  label(card,mode==2?"Nhận liên kết đặt lại mật khẩu qua email.":"Lưu phim yêu thích và tiếp tục câu chuyện của bạn.",14,0xffc4b6b7);
  EditText email=field(card,"Email",false),password=mode==2?null:field(card,"Mật khẩu",true),confirm=mode==1?field(card,"Nhập lại mật khẩu",true):null;
  if(mode==0)link(card,"Quên mật khẩu?",()->a.startActivity(new Intent(a,ForgotPasswordActivity.class)));
  TextView status=label(card,"",13,0xffffb4aa);status.setPadding(0,(int)(12*d),0,(int)(12*d));status.setAccessibilityLiveRegion(1);
  Button submit=new Button(a);submit.setText(mode==0?"Đăng nhập":mode==1?"Tạo tài khoản":"Gửi liên kết đặt lại");submit.setAllCaps(false);submit.setTextColor(0xffffffff);submit.setTextSize(16);GradientDrawable accent=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffbe1020,0xffee3541});accent.setCornerRadius(14*d);submit.setBackground(accent);submit.setBackgroundTintList(null);card.addView(submit,new LinearLayout.LayoutParams(-1,(int)(52*d)));
  submit.setOnClickListener(v->{String address=email.getText().toString().trim();if(!android.util.Patterns.EMAIL_ADDRESS.matcher(address).matches()){email.setError("Nhập email hợp lệ");return;}
   if(mode!=2&&(password.length()==0||(mode==1&&password.length()<6))){password.setError("Mật khẩu đăng ký cần ít nhất 6 ký tự");return;}
   if(mode==1&&!password.getText().toString().equals(confirm.getText().toString())){confirm.setError("Hai mật khẩu chưa khớp");return;}
   submit.setEnabled(false);status.setText("Đang xử lý…");
   com.google.android.gms.tasks.Task<?> task=mode==2?auth.sendPasswordResetEmail(address):mode==1?auth.createUserWithEmailAndPassword(address,password.getText().toString()):auth.signInWithEmailAndPassword(address,password.getText().toString());
   task.addOnCompleteListener(t->{if(a.isFinishing())return;submit.setEnabled(true);if(t.isSuccessful()){if(mode==2)status.setText("Nếu email đã đăng ký, hãy kiểm tra hộp thư và thư rác để đặt lại mật khẩu.");else home.run();}else status.setText("Chưa thực hiện được. Kiểm tra thông tin và kết nối rồi thử lại.");});
  });
  link(card,mode==0?"Chưa có tài khoản?  Đăng ký":"Quay lại đăng nhập",()->{if(mode==0)a.startActivity(new Intent(a,SignUpActivity.class));else a.finish();});
  link(root,"Khám phá với tư cách khách →",home);
  if(mode==0)link(root,"Admin · Điền sẵn email thử nghiệm",()->{email.setText("admin@gmail.com");password.requestFocus();status.setText("Nhập mật khẩu của admin@gmail.com, rồi bấm Đăng nhập.");});
  a.setContentView(scroll);androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(scroll,(v,i)->{androidx.core.graphics.Insets bars=i.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars()|androidx.core.view.WindowInsetsCompat.Type.ime());v.setPadding(bars.left,bars.top,bars.right,bars.bottom);return i;});
 }
 static TextView label(LinearLayout p,String s,int size,int color){TextView t=new TextView(p.getContext());t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setLineSpacing(5,1);p.addView(t);return t;}
 static void link(LinearLayout p,String s,Runnable action){TextView t=label(p,s,14,0xffff8189);int pad=(int)(16*p.getResources().getDisplayMetrics().density);t.setPadding(0,pad,0,pad);t.setGravity(17);t.setOnClickListener(v->action.run());}
 static EditText field(LinearLayout p,String hint,boolean secret){TextInputLayout box=new TextInputLayout(p.getContext());box.setHint(hint);box.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);box.setBoxStrokeColor(0xffe23c47);box.setDefaultHintTextColor(android.content.res.ColorStateList.valueOf(0xffc7afb1));TextInputEditText input=new TextInputEditText(box.getContext());input.setInputType(secret?129:33);input.setSingleLine(true);input.setTextColor(-1);box.addView(input);if(secret)box.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=(int)(18*p.getResources().getDisplayMetrics().density);p.addView(box,lp);return input;}
}
