package com.example.pgck2;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
public class LoginActivity extends AppCompatActivity {
 private FirebaseAuth auth; private EditText email,password; private TextView status;
 private Button login,register,reset;
 @Override public void onCreate(Bundle state){
  super.onCreate(state); auth=FirebaseAuth.getInstance();
  AuthScreen.show(this,0);
  if(true)return;
  if(auth.getCurrentUser()!=null){home();return;}
  LinearLayout box=new LinearLayout(this);box.setOrientation(1);
  int p=(int)(24*getResources().getDisplayMetrics().density);box.setPadding(p,p*2,p,p);box.setBackgroundColor(0xff121212);
  TextView title=new TextView(this);title.setText("PGCK2 CINEMA — Tài khoản");title.setTextSize(26);title.setTextColor(0xffe50914);box.addView(title);
  email=new EditText(this);email.setHint("Email");email.setInputType(33);
  password=new EditText(this);password.setHint("Mật khẩu");password.setInputType(129);
  for(EditText field:new EditText[]{email,password}){field.setSingleLine(true);field.setTextColor(-1);field.setHintTextColor(0xffaaaaaa);box.addView(field);}
  login=button(box,"Đăng nhập");register=button(box,"Đăng ký");reset=button(box,"Quên mật khẩu");
  Button guest=button(box,"Khám phá không cần tài khoản");guest.setOnClickListener(v->home());
  Button admin=button(box,"Tài khoản Admin · Test");admin.setOnClickListener(v->{email.setText("admin@gmail.com");password.requestFocus();});
  status=new TextView(this);status.setTextColor(-1);box.addView(status);
  ScrollView scroll=new ScrollView(this);scroll.addView(box);setContentView(scroll);
  title.setText("CINEMA\nChào mừng bạn trở lại");
  CinemaStyle.apply(this);
  login.setOnClickListener(v->submit(false));register.setOnClickListener(v->submit(true));
  reset.setOnClickListener(v->{if(!validEmail())return;busy(true);auth.sendPasswordResetEmail(email.getText().toString().trim()).addOnCompleteListener(t->{busy(false);status.setText(t.isSuccessful()?"Nếu email đã đăng ký, hãy kiểm tra thư đặt lại mật khẩu.":"Không gửi được email. Kiểm tra kết nối và thử lại.");});});
 }
 private Button button(LinearLayout box,String text){Button b=new Button(this);b.setText(text);box.addView(b);return b;}
 private boolean validEmail(){if(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())return true;email.setError("Email không hợp lệ");return false;}
 private void busy(boolean b){login.setEnabled(!b);register.setEnabled(!b);reset.setEnabled(!b);status.setText(b?"Đang xử lý…":"");}
 private void submit(boolean create){
  if(!validEmail())return;String secret=password.getText().toString();if(secret.isEmpty()||(create&&secret.length()<6)){password.setError("Mật khẩu đăng ký cần ít nhất 6 ký tự");return;}
  busy(true);String address=email.getText().toString().trim();
  com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> task=create?auth.createUserWithEmailAndPassword(address,secret):auth.signInWithEmailAndPassword(address,secret);
  task.addOnCompleteListener(t->{if(t.isSuccessful())home();else{busy(false);status.setText("Không thành công. Kiểm tra email, mật khẩu, kết nối và cấu hình Firebase Authentication.");}});
 }
 private void home(){startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();}
}
