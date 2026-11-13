package com.example.pgck2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

/** A title whose shader is rebuilt for the actual text width, including recycled banners. */
public class MovieTitleView extends AppCompatTextView {
    private String startColor = "#FFFFFF";
    private String endColor = "";

    public MovieTitleView(Context context) { super(context); }
    public MovieTitleView(Context context, AttributeSet attrs) { super(context, attrs); }
    public MovieTitleView(Context context, AttributeSet attrs, int style) { super(context, attrs, style); }

    static boolean validColor(String value) {
        return value != null && value.matches("#[0-9a-fA-F]{6}");
    }

    public void setTitleColors(String start, String end) {
        startColor = validColor(start) ? start : "#FFFFFF";
        endColor = validColor(end) ? end : "";
        getPaint().setShader(null);
        setTextColor(Color.parseColor(startColor));
        invalidate();
    }

    @Override protected void onDraw(Canvas canvas) {
        if (validColor(endColor)) {
            float width = Math.min(getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight(),
                    getPaint().measureText(getText().toString()));
            getPaint().setShader(new LinearGradient(0, 0, Math.max(1, width), 0,
                    Color.parseColor(startColor), Color.parseColor(endColor), Shader.TileMode.CLAMP));
        } else {
            getPaint().setShader(null);
        }
        super.onDraw(canvas);
    }
}
