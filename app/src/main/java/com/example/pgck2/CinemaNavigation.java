package com.example.pgck2;
import android.content.Intent;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

final class CinemaNavigation {
 static void attach(AppCompatActivity activity,android.view.ViewGroup root,int selected,java.util.function.IntConsumer action){
  float d=activity.getResources().getDisplayMetrics().density;
  LinearLayout bar=new LinearLayout(activity);bar.setTag("cinema_navigation");bar.setBackgroundColor(0xff111111);bar.setElevation(10*d);
  String[] names={"Trang chủ","Kho phim","Tủ phim","Cá nhân"};
  int[] icons={R.drawable.ic_nav_home,R.drawable.ic_nav_explore,R.drawable.ic_nav_library,R.drawable.ic_nav_account};
  for(int i=0;i<4;i++){final int tab=i;LinearLayout item=new LinearLayout(activity);item.setOrientation(1);item.setGravity(Gravity.CENTER);item.setContentDescription(names[i]);
   ImageView icon=new ImageView(activity);icon.setImageResource(icons[i]);icon.setImageTintList(android.content.res.ColorStateList.valueOf(i==selected?0xffe50914:0xff929292));item.addView(icon,new LinearLayout.LayoutParams((int)(25*d),(int)(25*d)));
   TextView label=new TextView(activity);label.setText(names[i]);label.setTextSize(11);label.setTextColor(i==selected?0xffe50914:0xff929292);label.setPadding(0,(int)(5*d),0,0);item.addView(label);
   item.setBackgroundResource(android.R.drawable.list_selector_background);bar.addView(item,new LinearLayout.LayoutParams(0,(int)(72*d),1));
   item.setOnClickListener(v->{if(tab<2)for(int j=0;j<bar.getChildCount();j++){LinearLayout cell=(LinearLayout)bar.getChildAt(j);int color=j==tab?0xffe50914:0xff929292;((ImageView)cell.getChildAt(0)).setImageTintList(android.content.res.ColorStateList.valueOf(color));((TextView)cell.getChildAt(1)).setTextColor(color);}action.accept(tab);});
  }
  androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams p=new androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams(-1,(int)(72*d));p.gravity=Gravity.BOTTOM;root.addView(bar,p);
 }
 static void select(android.view.View root,int selected){
  LinearLayout bar=root.findViewWithTag("cinema_navigation");if(bar==null)return;
  for(int i=0;i<bar.getChildCount();i++){LinearLayout item=(LinearLayout)bar.getChildAt(i);int color=i==selected?0xffe50914:0xff929292;((ImageView)item.getChildAt(0)).setImageTintList(android.content.res.ColorStateList.valueOf(color));((TextView)item.getChildAt(1)).setTextColor(color);}
 }
}
