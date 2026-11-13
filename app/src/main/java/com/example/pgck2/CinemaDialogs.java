package com.example.pgck2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.*;
import com.google.firebase.auth.*;

final class CinemaDialogs {
    static void menu(AppCompatActivity activity,String title,String[] options,java.util.function.IntConsumer selected){
        BottomSheetDialog sheet=new BottomSheetDialog(activity);LinearLayout body=new LinearLayout(activity);body.setOrientation(1);int pad=Math.round(24*activity.getResources().getDisplayMetrics().density);body.setPadding(pad,pad,pad,pad);
        GradientDrawable bg=new GradientDrawable();bg.setColor(0xff191919);bg.setCornerRadii(new float[]{pad,pad,pad,pad,0,0,0,0});body.setBackground(bg);
        TextView heading=new TextView(activity);heading.setText(title);heading.setTextSize(21);heading.setTextColor(-1);heading.setTypeface(null,1);heading.setPadding(0,0,0,pad);body.addView(heading);
        for(int i=0;i<options.length;i++){final int index=i;TextView row=new TextView(activity);row.setText(options[i]+"  ›");row.setTextColor(0xffeeeeee);row.setTextSize(16);row.setPadding(0,pad*2/3,0,pad*2/3);row.setBackgroundResource(android.R.drawable.list_selector_background);body.addView(row);row.setOnClickListener(v->{sheet.dismiss();selected.accept(index);});}
        ScrollView scroll=new ScrollView(activity);scroll.setFillViewport(false);scroll.addView(body);sheet.setContentView(scroll);sheet.show();
    }
    static void account(AppCompatActivity activity){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();if(user==null){activity.startActivity(new Intent(activity,LoginActivity.class));return;}
        menu(activity,user.getDisplayName()==null?user.getEmail():user.getDisplayName(),new String[]{"Hồ sơ cá nhân","Đặt lại mật khẩu","Trang chủ và quản trị","Đăng xuất","Premium · gói tháng/năm"},which->{
            if(which==0)profile(activity,user);
            else if(which==1)new MaterialAlertDialogBuilder(activity).setTitle("Đặt lại mật khẩu").setMessage("Gửi liên kết đặt lại tới "+user.getEmail()+"?").setNegativeButton("Hủy",null).setPositiveButton("Gửi email",(d,w)->FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(t->Toast.makeText(activity,t.isSuccessful()?"Đã gửi email. Kiểm tra cả thư rác.":"Chưa gửi được. Thử lại.",Toast.LENGTH_LONG).show())).show();
            else if(which==2){if(activity instanceof MainActivity){activity.findViewById(R.id.btnToggleAdmin).performClick();}else{activity.startActivity(new Intent(activity,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));activity.finish();}}
            else if(which==4)activity.startActivity(new Intent(activity,PremiumActivity.class));
            else SessionNavigation.confirmLogout(activity);
        });
    }
    private static void profile(AppCompatActivity activity,FirebaseUser user){
        LinearLayout body=new LinearLayout(activity);body.setOrientation(1);int pad=Math.round(24*activity.getResources().getDisplayMetrics().density);body.setPadding(pad,0,pad,0);
        TextInputEditText name=input(body,"Tên hiển thị",user.getDisplayName());TextInputEditText image=input(body,"Ảnh đại diện · URL HTTPS",user.getPhotoUrl()==null?"":user.getPhotoUrl().toString());
        androidx.appcompat.app.AlertDialog dialog=new MaterialAlertDialogBuilder(activity).setTitle("Hồ sơ cá nhân").setView(body).setPositiveButton("Lưu thay đổi",null).setNegativeButton("Hủy",null).create();dialog.show();dialog.getButton(-1).setOnClickListener(v->{String label=name.getText().toString().trim(),url=image.getText().toString().trim();if(label.isEmpty()||label.length()>80){name.setError("Tên từ 1 đến 80 ký tự");return;}if(!url.isEmpty()&&(!url.startsWith("https://")||!android.util.Patterns.WEB_URL.matcher(url).matches())){image.setError("Nhập URL HTTPS hợp lệ");return;}dialog.getButton(-1).setEnabled(false);user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(label).setPhotoUri(url.isEmpty()?null:android.net.Uri.parse(url)).build()).addOnCompleteListener(t->{dialog.getButton(-1).setEnabled(true);if(t.isSuccessful())dialog.dismiss();Toast.makeText(activity,t.isSuccessful()?"Đã lưu hồ sơ":"Chưa lưu được hồ sơ",Toast.LENGTH_SHORT).show();});});
    }
    static TextInputEditText input(LinearLayout parent,String label,String value){TextInputLayout field=new TextInputLayout(parent.getContext());float density=parent.getResources().getDisplayMetrics().density;field.setHint(label);field.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);field.setBoxCornerRadii(12*density,12*density,12*density,12*density);field.setDefaultHintTextColor(ColorStateList.valueOf(0xffb6b4c8));field.setHintTextColor(ColorStateList.valueOf(0xff49d4b6));field.setBoxStrokeColor(0xff49d4b6);field.setBoxBackgroundColor(0xff242532);TextInputEditText input=new TextInputEditText(field.getContext());input.setText(value);input.setTextColor(-1);field.addView(input);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=Math.round(16*density);parent.addView(field,p);return input;}
}
