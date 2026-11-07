package com.example.pgck2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

/** Library sections use the same account-scoped collections as the playback screen. */
public class LibraryActivity extends AppCompatActivity {
    private LinearLayout content;
    private final List<ListenerRegistration> listeners=new ArrayList<>();
    private LinearLayout continueRows,savedRows,historyRows;
    private TextView continueState,savedState,historyState;
    private int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){startActivity(new Intent(this,LoginActivity.class));finish();return;}
        CoordinatorLayout root=new CoordinatorLayout(this);root.setBackgroundColor(0xff111111);
        LinearLayout page=new LinearLayout(this);page.setOrientation(1);
        CoordinatorLayout.LayoutParams pageParams=new CoordinatorLayout.LayoutParams(-1,-1);pageParams.bottomMargin=dp(72);root.addView(page,pageParams);
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);header.setPadding(dp(20),dp(16),dp(20),dp(16));page.addView(header);
        TextView logo=label("CINEMA",27,0xffe50914);logo.setTypeface(null,1);header.addView(logo,new LinearLayout.LayoutParams(0,-2,1));
        ImageButton search=new ImageButton(this);search.setImageResource(R.drawable.ic_search);search.setImageTintList(ColorStateList.valueOf(-1));search.setBackgroundColor(0x00000000);search.setContentDescription("Tìm phim");header.addView(search,new LinearLayout.LayoutParams(dp(48),dp(48)));search.setOnClickListener(v->home(true,false));
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);page.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        content=new LinearLayout(this);content.setOrientation(1);content.setPadding(dp(16),dp(16),dp(16),dp(32));scroll.addView(content);
        TextView title=label("Tủ phim của bạn",27,-1);title.setTypeface(null,1);content.addView(title);
        TextView subtitle=label("Những câu chuyện bạn muốn quay lại",14,0xff989898);subtitle.setPadding(0,dp(8),0,dp(8));content.addView(subtitle);
        continueState=section("TIẾP TỤC XEM");HorizontalScrollView horizontal=new HorizontalScrollView(this);horizontal.setHorizontalScrollBarEnabled(false);continueRows=new LinearLayout(this);horizontal.addView(continueRows);content.addView(horizontal);
        savedState=section("PHIM ĐÃ LƯU");savedRows=new LinearLayout(this);savedRows.setOrientation(1);content.addView(savedRows);
        historyState=section("LỊCH SỬ XEM");historyRows=new LinearLayout(this);historyRows.setOrientation(1);content.addView(historyRows);
        CinemaNavigation.attach(this,root,2,tab->{if(tab==0)home(false,false);else if(tab==1)home(false,true);else if(tab==3)CinemaDialogs.account(this);});
        setContentView(root);CinemaStyle.applyInsets(root);
    }
    private TextView label(String value,int size,int color){TextView text=new TextView(this);text.setText(value);text.setTextSize(size);text.setTextColor(color);return text;}
    private TextView section(String title){TextView heading=label(title,15,0xffaaaaaa);heading.setTypeface(null,1);heading.setPadding(0,dp(28),0,dp(14));content.addView(heading);TextView state=label("Đang tải…",14,0xff888888);content.addView(state);return state;}
    private void home(boolean search,boolean catalog){startActivity(new Intent(this,MainActivity.class).putExtra("open_search",search).putExtra("open_catalog",catalog).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));finish();}
    @Override protected void onStart(){super.onStart();if(FirebaseAuth.getInstance().getCurrentUser()==null){finish();return;}listen();}
    @Override protected void onStop(){super.onStop();for(ListenerRegistration listener:listeners)listener.remove();listeners.clear();}
    private void listen(){
        listeners.add(ViewerLibrary.collection("watchlist").orderBy("updatedAt",Query.Direction.DESCENDING).addSnapshotListener((snapshot,error)->{
            if(error!=null){savedState.setVisibility(View.VISIBLE);savedState.setText("Không tải được phim đã lưu. Chạm để thử lại.");savedState.setOnClickListener(v->retry());return;}
            if(snapshot==null)return;savedRows.removeAllViews();LinearLayout row=null;int count=0;
            for(DocumentSnapshot doc:snapshot){Video movie=doc.get("video",Video.class);if(movie==null)continue;if(count%3==0){row=new LinearLayout(this);savedRows.addView(row);}MaterialCardView card=poster(movie,doc,false);
                LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);p.setMargins(0,0,dp(8),dp(12));row.addView(card,p);count++;}
            if(row!=null&&count%3!=0)for(int i=count%3;i<3;i++)row.addView(new View(this),new LinearLayout.LayoutParams(0,0,1));
            savedState.setText("Lưu phim từ trang chi tiết để xem tại đây.");savedState.setVisibility(count==0?View.VISIBLE:View.GONE);
        }));
        listeners.add(ViewerLibrary.collection("history").orderBy("updatedAt",Query.Direction.DESCENDING).addSnapshotListener((snapshot,error)->{
            if(error!=null){for(TextView state:new TextView[]{continueState,historyState}){state.setVisibility(View.VISIBLE);state.setText("Không tải được lịch sử. Chạm để thử lại.");state.setOnClickListener(v->retry());}return;}
            if(snapshot==null)return;continueRows.removeAllViews();historyRows.removeAllViews();Set<String> shown=new HashSet<>();int ongoing=0,total=0;
            for(DocumentSnapshot doc:snapshot){Video movie=doc.get("video",Video.class);if(movie==null)continue;total++;
                if(!Boolean.TRUE.equals(doc.getBoolean("completed"))&&shown.add(movie.getId())){MaterialCardView card=poster(movie,doc,true);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(dp(210),-2);p.rightMargin=dp(12);continueRows.addView(card,p);ongoing++;}
                historyRows.addView(historyItem(movie,doc));
            }
            continueState.setText("Phim đang xem sẽ xuất hiện tại đây.");continueState.setVisibility(ongoing==0?View.VISIBLE:View.GONE);historyState.setText("Bạn chưa xem tập phim nào.");historyState.setVisibility(total==0?View.VISIBLE:View.GONE);
        }));
    }
    private void retry(){for(ListenerRegistration l:listeners)l.remove();listeners.clear();listen();}
    private MaterialCardView poster(Video movie,DocumentSnapshot doc,boolean ongoing){
        MaterialCardView card=new MaterialCardView(this);card.setCardBackgroundColor(0xff1c1c1c);card.setRadius(dp(7));card.setStrokeWidth(0);card.setCardElevation(0);
        LinearLayout body=new LinearLayout(this);body.setOrientation(1);card.addView(body);
        FrameLayout imageFrame=new FrameLayout(this);body.addView(imageFrame,new LinearLayout.LayoutParams(-1,dp(ongoing?118:164)));
        ImageView image=new ImageView(this);image.setScaleType(ImageView.ScaleType.CENTER_CROP);image.setContentDescription(movie.getTitle());imageFrame.addView(image,new FrameLayout.LayoutParams(-1,-1));com.bumptech.glide.Glide.with(this).load(movie.getThumbnailUrl()).placeholder(R.drawable.bg_gradient_top_down).into(image);
        if(ongoing){ImageView play=new ImageView(this);play.setImageResource(R.drawable.ic_play);play.setImageTintList(ColorStateList.valueOf(-1));FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(dp(42),dp(42),Gravity.CENTER);imageFrame.addView(play,p);LinearProgressIndicator progress=new LinearProgressIndicator(this);progress.setMax(100);Long duration=doc.getLong("duration"),position=doc.getLong("position");progress.setProgress(duration!=null&&duration>0&&position!=null?(int)Math.min(100,position*100/duration):0);progress.setIndicatorColor(0xffe50914);progress.setTrackColor(0xff383838);body.addView(progress,new LinearLayout.LayoutParams(-1,dp(4)));}
        TextView title=label(movie.getTitle(),13,-1);title.setMaxLines(2);title.setEllipsize(android.text.TextUtils.TruncateAt.END);title.setPadding(dp(8),dp(9),dp(8),dp(8));body.addView(title);
        if(ongoing){TextView episode=label("Tập "+doc.getLong("episodeNumber"),12,0xffaaaaaa);episode.setPadding(dp(8),0,dp(8),dp(10));body.addView(episode);}
        card.setOnClickListener(v->openMovie(movie,doc,ongoing));card.setContentDescription((ongoing?"Tiếp tục xem ":"Chi tiết ")+movie.getTitle());
        if(!ongoing)card.setOnLongClickListener(v->{CinemaDialogs.menu(this,"Phim đã lưu",new String[]{"Xem chi tiết","Bỏ lưu phim"},which->{if(which==0)openMovie(movie,doc,false);else doc.getReference().delete().addOnFailureListener(e->Toast.makeText(this,"Chưa bỏ lưu được. Thử lại.",Toast.LENGTH_SHORT).show());});return true;});
        return card;
    }
    private View historyItem(Video movie,DocumentSnapshot doc){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(0,dp(8),0,dp(8));ImageView image=new ImageView(this);image.setScaleType(ImageView.ScaleType.CENTER_CROP);row.addView(image,new LinearLayout.LayoutParams(dp(94),dp(60)));com.bumptech.glide.Glide.with(this).load(movie.getThumbnailUrl()).into(image);
        LinearLayout info=new LinearLayout(this);info.setOrientation(1);info.setPadding(dp(12),0,0,0);row.addView(info,new LinearLayout.LayoutParams(0,-2,1));info.addView(label(movie.getTitle(),14,-1));String date=doc.getTimestamp("updatedAt")==null?"":android.text.format.DateFormat.format("dd/MM/yyyy · HH:mm",doc.getTimestamp("updatedAt").toDate()).toString();info.addView(label("Tập "+doc.getLong("episodeNumber")+" · "+(Boolean.TRUE.equals(doc.getBoolean("completed"))?"Đã xem":"Đang xem"),12,0xffaaaaaa));info.addView(label(date,11,0xff888888));row.setOnClickListener(v->openMovie(movie,doc,true));return row;
    }
    private void openMovie(Video movie,DocumentSnapshot history,boolean resume){
        FirebaseFirestore.getInstance().collection("videos").document(movie.getId()).get().addOnSuccessListener(current->{if(isFinishing())return;Video latest=current.exists()?current.toObject(Video.class):movie;if(latest==null)return;latest.setId(movie.getId());
            if(resume){Long number=history.getLong("episodeNumber");for(Episode ep:latest.getEpisodes())if(number!=null&&ep.getEpisodeNumber()==number){startActivity(PlaybackIntents.episode(this,latest,ep));return;}}
            new MovieDetailBottomSheetDialog(latest).show(getSupportFragmentManager(),"detail");
        }).addOnFailureListener(e->Toast.makeText(this,"Không mở được phim. Vui lòng thử lại.",Toast.LENGTH_SHORT).show());
    }
}
