package com.example.pgck2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import java.util.*;

/** Native grouped bars; the dashboard also exposes every value in a text sheet. */
final class StatsTimelineView extends View {
    static final int VIEW_COLOR=0xffff5364,LIKE_COLOR=0xff49d4b6,DISLIKE_COLOR=0xffffbf66;
    private final List<Map.Entry<String,StatsReport.Numbers>> points;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private final long maximum;
    StatsTimelineView(Context context,Map<String,StatsReport.Numbers> data){
        super(context);density=getResources().getDisplayMetrics().density;points=new ArrayList<>(data.entrySet());
        long max=1;for(Map.Entry<String,StatsReport.Numbers> p:points){StatsReport.Numbers n=p.getValue();max=Math.max(max,Math.max(n.views,Math.max(n.likes,n.dislikes)));}maximum=Math.max(4,(long)Math.ceil(max/4.0)*4);
        setContentDescription("Biểu đồ lượt xem màu đỏ, lượt thích mới màu xanh ngọc, lượt không thích mới màu vàng. Dùng nút Chi tiết biểu đồ để đọc số liệu.");
    }
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);float left=40*density,right=getWidth()-12*density,top=24*density,bottom=getHeight()-34*density;
        paint.setTextSize(10*density);paint.setStrokeWidth(density);
        for(int i=0;i<=4;i++){float y=bottom-(bottom-top)*i/4;paint.setColor(0xff343542);canvas.drawLine(left,y,right,y,paint);paint.setColor(0xffa7a9b7);paint.setTextAlign(Paint.Align.RIGHT);canvas.drawText(String.valueOf((long)Math.ceil(maximum*i/4.0)),left-6*density,y+4*density,paint);}
        if(points.isEmpty())return;float slot=(right-left)/points.size(),bar=Math.min(10*density,slot/4);
        int[] colors={VIEW_COLOR,LIKE_COLOR,DISLIKE_COLOR};
        for(int i=0;i<points.size();i++){
            StatsReport.Numbers n=points.get(i).getValue();long[] values={n.views,n.likes,n.dislikes};float center=left+slot*(i+.5f);
            for(int j=0;j<3;j++){float height=(bottom-top)*values[j]/maximum;paint.setColor(colors[j]);float x=center+(j-1)*bar;canvas.drawRoundRect(x-bar*.4f,bottom-height,x+bar*.4f,bottom,2*density,2*density,paint);}
            String key=points.get(i).getKey();String label=key.length()>7?key.substring(8):key.substring(5)+"/"+key.substring(2,4);
            paint.setColor(0xffa7a9b7);paint.setTextAlign(Paint.Align.CENTER);canvas.drawText(label,center,bottom+20*density,paint);
        }
    }
}
