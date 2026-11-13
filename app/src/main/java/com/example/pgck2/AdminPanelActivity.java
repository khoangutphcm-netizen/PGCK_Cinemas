package com.example.pgck2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.*;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import java.util.*;

/** Separate admin surface; the inherited editor still owns upload and episode forms. */
public class AdminPanelActivity extends MainActivity {
    private LinearLayout rows,tools;
    private TextView status;
    private EditText search;
    private MaterialButton retry;
    private String tab="stats",verifiedUid;
    private boolean verified,active,loading;
    private int generation;
    private final List<DocumentSnapshot> cache=new ArrayList<>();
    private final FirebaseFirestore database=FirebaseFirestore.getInstance();
    private int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
    private TextView text(String value,int size,int color){TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(color);return t;}
    private LinearLayout column(){LinearLayout box=new LinearLayout(this);box.setOrientation(1);return box;}
    private MaterialButton button(String title,int tint){MaterialButton b=new MaterialButton(this);b.setText(title);b.setTextSize(12);b.setTextColor(-1);b.setCornerRadius(dp(12));b.setAllCaps(false);b.setBackgroundTintList(ColorStateList.valueOf(tint));b.setMinWidth(0);b.setMinimumWidth(0);return b;}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);if(state!=null)tab=state.getString("adminTab","stats");
        LinearLayout root=column();root.setId(R.id.main);root.setBackgroundColor(0xff101119);root.setPadding(dp(18),dp(18),dp(18),dp(12));
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);root.addView(header);
        LinearLayout headings=column();header.addView(headings,new LinearLayout.LayoutParams(0,-2,1));
        TextView brand=text("PCGK CINEMA  /  ADMIN",11,0xffff7a87);brand.setLetterSpacing(.08f);headings.addView(brand);
        TextView heading=text("Quản lý rạp phim",23,-1);heading.setTypeface(null,1);headings.addView(heading);
        TextView intro=text("Nội dung · Thành viên · Báo cáo",12,0xffa9b2c4);intro.setPadding(0,dp(5),0,dp(12));headings.addView(intro);
        MaterialButton back=button("Về app",0xff303744);header.addView(back);back.setOnClickListener(v->finish());
        ChipGroup tabs=new ChipGroup(this);tabs.setSingleSelection(true);tabs.setSelectionRequired(true);String[] keys={"stats","videos","users"},titles={"Thống kê","Video","Tài khoản"};Map<Integer,String> choices=new HashMap<>();for(int i=0;i<keys.length;i++){Chip chip=new Chip(this);chip.setId(View.generateViewId());chip.setText(titles[i]);chip.setCheckable(true);chip.setTextColor(-1);chip.setChipBackgroundColor(new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{0xff84273e,0xff282a38}));tabs.addView(chip);choices.put(chip.getId(),keys[i]);if(tab.equals(keys[i]))tabs.check(chip.getId());}root.addView(tabs);tabs.setOnCheckedStateChangeListener((g,ids)->{if(ids.isEmpty())return;tab=choices.get(ids.get(0));cache.clear();load();});
        search=new EditText(this);search.setSingleLine(true);search.setTextSize(14);search.setTextColor(-1);root.addView(search,new LinearLayout.LayoutParams(-1,dp(54)));if(state!=null)search.setText(state.getString("adminSearch",""));search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int f){}public void onTextChanged(CharSequence s,int a,int b,int c){if(verified&&!loading)render();}public void afterTextChanged(android.text.Editable e){}});
        tools=new LinearLayout(this);root.addView(tools);LinearLayout stateRow=new LinearLayout(this);stateRow.setGravity(Gravity.CENTER_VERTICAL);root.addView(stateRow);status=text("",12,0xffa9b2c4);stateRow.addView(status,new LinearLayout.LayoutParams(0,-2,1));retry=button("Làm mới",0xff29374e);stateRow.addView(retry);retry.setOnClickListener(v->verify());
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setClipToPadding(false);scroll.setPadding(0,0,0,dp(16));rows=column();scroll.addView(rows);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout safeArea=column();safeArea.setBackgroundColor(0xff101119);safeArea.addView(root,new LinearLayout.LayoutParams(-1,-1));setContentView(safeArea);CinemaStyle.applyInsets(safeArea);
    }
    @Override protected void onResume(){super.onResume();active=true;verify();}
    @Override protected void onPause(){active=false;verified=false;generation++;super.onPause();}
    @Override protected void onSaveInstanceState(Bundle state){super.onSaveInstanceState(state);state.putString("adminTab",tab);state.putString("adminSearch",search.getText().toString());}
    private boolean sameSession(){FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();return active&&!isFinishing()&&user!=null&&user.getUid().equals(verifiedUid);}
    private void verify(){
        if(status==null||!active)return;FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();if(user==null){SessionNavigation.welcome(this);return;}int token=++generation;verified=false;loading=true;rows.removeAllViews();tools.removeAllViews();retry.setEnabled(false);search.setEnabled(false);status.setText("Đang xác minh quyền quản trị…");String uid=user.getUid();
        database.collection("users").document(uid).get(Source.SERVER).addOnSuccessListener(doc->{FirebaseUser current=FirebaseAuth.getInstance().getCurrentUser();if(!active||isFinishing()||token!=generation||current==null||!uid.equals(current.getUid()))return;if(!"admin".equals(doc.getString("role"))){Toast.makeText(this,"Tài khoản không còn quyền quản trị",Toast.LENGTH_LONG).show();finish();return;}verified=true;verifiedUid=uid;load();}).addOnFailureListener(e->{if(active&&token==generation){loading=false;retry.setEnabled(true);status.setText("Chưa xác minh được quyền. Bấm Làm mới để thử lại.");}});
    }
    @Override protected void onCatalogChanged(){if(verified&&sameSession())load();}
    @Override protected boolean canEditCatalog(){return verified&&sameSession();}
    private void load(){
        if(!verified||!sameSession())return;int token=++generation;loading=true;rows.removeAllViews();tools.removeAllViews();cache.clear();search.setEnabled(true);search.setVisibility(tab.equals("stats")?View.GONE:View.VISIBLE);search.setHint(tab.equals("users")?"Tìm theo email, tên hoặc UID":"Tìm tên phim, mô tả hoặc thể loại");
        if(tab.equals("stats")){loading=false;retry.setEnabled(true);render();return;}
        if(tab.equals("videos")){MaterialButton add=button("+ Thêm phim mới",0xffe50924),genres=button("Thể loại",0xff24514b);LinearLayout.LayoutParams addParams=new LinearLayout.LayoutParams(0,-2,1);addParams.rightMargin=dp(8);tools.addView(add,addParams);tools.addView(genres,new LinearLayout.LayoutParams(0,-2,1));add.setOnClickListener(v->{if(verified&&sameSession())showAddEditVideoDialog(null);});genres.setOnClickListener(v->{if(verified&&sameSession())showAdminManageGenresDialog(new TextView(this),new ArrayList<>());});}
        retry.setEnabled(false);status.setText("Đang tải danh sách…");String collection=tab;
        database.collection(collection).get(Source.SERVER).addOnSuccessListener(snapshot->{if(!sameSession()||token!=generation)return;cache.addAll(snapshot.getDocuments());loading=false;retry.setEnabled(true);render();}).addOnFailureListener(e->{if(!sameSession()||token!=generation)return;loading=false;search.setEnabled(false);retry.setEnabled(true);status.setText("Không tải được dữ liệu. Kiểm tra kết nối/Rules rồi bấm Làm mới.");});
    }
    private MaterialCardView card(LinearLayout content,int tint){MaterialCardView card=new MaterialCardView(this);card.setRadius(dp(16));card.setCardElevation(0);card.setCardBackgroundColor(tint);card.setStrokeWidth(dp(1));card.setStrokeColor(0xff414453);card.addView(content);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(12);rows.addView(card,p);return card;}
    private void reportCard(String title,String detail,int color,Runnable open){LinearLayout content=column();content.setPadding(dp(18),dp(20),dp(18),dp(18));TextView heading=text(title,21,-1);content.addView(heading);TextView caption=text(detail,14,0xffc4c6d2);caption.setPadding(0,dp(10),0,dp(14));content.addView(caption);MaterialButton action=button("Mở báo cáo →",0xff3d334e);content.addView(action);action.setOnClickListener(v->{if(verified&&sameSession())open.run();});card(content,color);}
    private void render(){
        if(!verified||!sameSession())return;rows.removeAllViews();if(tab.equals("stats")){status.setText("Báo cáo dành cho quản trị viên");reportCard("Hiệu quả nội dung","View, like/dislike, người xem, phân bố thể loại và xếp hạng theo tháng/năm.",0xff252238,()->startActivity(new Intent(this,AdminStatsActivity.class)));reportCard("Đơn Premium","Theo dõi đơn và giá trị thanh toán mô phỏng. Không phải doanh thu thật.",0xff34282a,()->startActivity(new Intent(this,PremiumOrdersActivity.class)));return;}
        int count=0;String query=search.getText().toString();for(DocumentSnapshot doc:cache){if(tab.equals("videos")){Video movie=doc.toObject(Video.class);if(movie==null||!CatalogSearch.matches(query,movie.getTitle(),movie.getDescription(),movie.getGenresString()))continue;movie.setId(doc.getId());videoCard(movie);}else{if(!CatalogSearch.matches(query,doc.getString("email"),doc.getString("displayName"),doc.getId()))continue;userCard(doc);}count++;}
        status.setText(count+" / "+cache.size()+" "+(tab.equals("videos")?"video":"hồ sơ"));if(count==0){TextView empty=text(cache.isEmpty()?"Chưa có dữ liệu. Thêm video hoặc kiểm tra hồ sơ trên Firebase.":"Không có kết quả. Thử từ khóa khác.",15,0xffb8bbc9);empty.setPadding(dp(8),dp(28),dp(8),0);rows.addView(empty);}
    }
    private void videoCard(Video movie){
        LinearLayout content=column();content.setPadding(dp(14),dp(14),dp(14),dp(10));LinearLayout overview=new LinearLayout(this);content.addView(overview);ImageView poster=new ImageView(this);poster.setScaleType(ImageView.ScaleType.CENTER_CROP);overview.addView(poster,new LinearLayout.LayoutParams(dp(62),dp(88)));com.bumptech.glide.Glide.with(this).load(movie.getThumbnailUrl()).placeholder(R.drawable.bg_gradient_top_down).into(poster);LinearLayout info=column();info.setPadding(dp(12),0,0,0);overview.addView(info,new LinearLayout.LayoutParams(0,-2,1));TextView title=text(movie.getTitle(),17,-1);title.setMaxLines(2);info.addView(title);info.addView(text(movie.getEpisodes().size()+" tập · "+movie.getGenresString(),12,0xffb3bbcd));info.addView(text((movie.isPremium()?"PREMIUM":"MIỄN PHÍ")+(movie.isPopular()?" · Nổi bật":""),11,movie.isPremium()?0xffffc476:0xff57d4bb));LinearLayout actions=new LinearLayout(this);content.addView(actions);MaterialButton edit=button("Chỉnh sửa",0xff394566),delete=button("Xóa",0xff6a2936);actions.addView(edit,new LinearLayout.LayoutParams(0,-2,1));actions.addView(delete,new LinearLayout.LayoutParams(0,-2,1));edit.setOnClickListener(v->{if(verified&&sameSession())showAddEditVideoDialog(movie);});delete.setOnClickListener(v->{if(verified&&sameSession())confirmDeleteVideo(movie);});card(content,0xff1d2231);
    }
    private void userCard(DocumentSnapshot user){LinearLayout content=column();content.setPadding(dp(16),dp(16),dp(16),dp(12));content.addView(text(user.getString("email")==null?"Hồ sơ chưa có email":user.getString("email"),16,-1));String name=user.getString("displayName");if(name!=null)content.addView(text(name,13,0xffb4c9c4));content.addView(text("admin".equals(user.getString("role"))?"QUẢN TRỊ VIÊN":"THÀNH VIÊN",11,0xff64d9ba));TextView id=text("UID: "+user.getId(),11,0xff929ba8);id.setMaxLines(1);id.setEllipsize(android.text.TextUtils.TruncateAt.END);content.addView(id);MaterialButton manage=button("Quản lý tài khoản",0xff255349);content.addView(manage);manage.setOnClickListener(v->{if(verified&&sameSession())manageUser(user);});card(content,0xff182a28);}
    private void manageUser(DocumentSnapshot user){CinemaDialogs.menu(this,user.getString("email")==null?"Hồ sơ tài khoản":user.getString("email"),new String[]{"Cấp quyền admin","Chuyển thành user","Gửi email đặt lại mật khẩu","Sao chép UID"},which->{
        if(!verified||!sameSession())return;if(which==3){((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(android.content.ClipData.newPlainText("UID",user.getId()));Toast.makeText(this,"Đã sao chép UID",Toast.LENGTH_SHORT).show();return;}
        if(which==1&&verifiedUid.equals(user.getId())){Toast.makeText(this,"Không thể tự gỡ quyền quản trị đang dùng",Toast.LENGTH_LONG).show();return;}String email=user.getString("email");if(which==2&&email==null){Toast.makeText(this,"Hồ sơ chưa có email để gửi thư",Toast.LENGTH_LONG).show();return;}
        new MaterialAlertDialogBuilder(this).setTitle("Xác nhận thay đổi").setMessage(which==2?"Gửi email đặt lại mật khẩu tới "+email+"?":"Thay đổi quyền của "+email+"?").setNegativeButton("Hủy",null).setPositiveButton("Xác nhận",(d,w)->{if(!verified||!sameSession())return;com.google.android.gms.tasks.Task<?> task=which==2?FirebaseAuth.getInstance().sendPasswordResetEmail(email):user.getReference().update("role",which==0?"admin":"user");task.addOnCompleteListener(t->{if(!sameSession())return;Toast.makeText(this,t.isSuccessful()?"Đã thực hiện":"Chưa thực hiện được. Kiểm tra quyền và kết nối.",Toast.LENGTH_LONG).show();if(t.isSuccessful())load();});}).show();
    });}
}
