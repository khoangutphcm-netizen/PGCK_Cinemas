package com.example.pgck2;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Shared surfaces and comfortable touch targets for account and library screens. */
final class CinemaStyle {
    static void applyInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root,(v,insets)->{
            androidx.core.graphics.Insets bars=insets.getInsets(WindowInsetsCompat.Type.systemBars()|WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left,bars.top,bars.right,bars.bottom);return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }
    static void apply(Activity activity) {
        ViewGroup content=activity.findViewById(android.R.id.content);
        View root=content.getChildAt(0);
        if(Boolean.TRUE.equals(root.getTag()))return;
        root.setTag(Boolean.TRUE);
        root.setBackgroundColor(Color.rgb(17,17,17));
        style(root);
        int left=root.getPaddingLeft(),top=root.getPaddingTop(),right=root.getPaddingRight(),bottom=root.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root,(v,insets)->{
            androidx.core.graphics.Insets bars=insets.getInsets(WindowInsetsCompat.Type.systemBars()|WindowInsetsCompat.Type.ime());
            v.setPadding(left+bars.left,top+bars.top,right+bars.right,bottom+bars.bottom);return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }
    static void style(View view) {
        float density=view.getResources().getDisplayMetrics().density;
        if(view instanceof TextView) {
            TextView text=(TextView)view;text.setTextColor(0xffedf0f8);
            text.setFontFeatureSettings("kern");
        }
        if(view instanceof EditText || view instanceof Button) {
            GradientDrawable bg=new GradientDrawable();bg.setColor(view instanceof EditText?0xff202020:0xff292929);
            bg.setCornerRadius(16*density);bg.setStroke((int)density,0xff37445c);
            view.setBackground(bg);view.setBackgroundTintList(null);
            view.setPadding((int)(18*density),(int)(12*density),(int)(18*density),(int)(12*density));
            view.setMinimumHeight((int)(52*density));
            if(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){ViewGroup.MarginLayoutParams p=(ViewGroup.MarginLayoutParams)view.getLayoutParams();p.topMargin=(int)(10*density);view.setLayoutParams(p);}
            if(view instanceof EditText)((EditText)view).setHintTextColor(0xff97a4bb);
            if(view instanceof Button){Button b=(Button)view;b.setAllCaps(false);b.setTextSize(15);b.setTypeface(null,Typeface.BOLD);}
        }
        if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)style(((ViewGroup)view).getChildAt(i));
    }
}
