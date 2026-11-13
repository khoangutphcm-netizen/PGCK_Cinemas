package com.example.pgck2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.android.gms.tasks.Task;
import java.util.*;

final class MovieEngagement {
 static Task<Void> react(Video video,String value){
  String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
  DocumentReference ref=FirebaseFirestore.getInstance().collection("reactions").document(uid+"_"+video.getId());
  DocumentReference event=FirebaseFirestore.getInstance().collection("reactionEvents").document();
  return FirebaseFirestore.getInstance().runTransaction(tx->{
   DocumentSnapshot old=tx.get(ref);String before=old.exists()?old.getString("value"):"none";String after=value.equals(before)?"none":value;
   if("none".equals(after))tx.delete(ref);else{Map<String,Object> data=new HashMap<>();data.put("uid",uid);data.put("videoId",video.getId());data.put("value",after);data.put("updatedAt",FieldValue.serverTimestamp());tx.set(ref,data);}
   Map<String,Object> data=new HashMap<>();data.put("uid",uid);data.put("videoId",video.getId());data.put("title",video.getTitle());data.put("genres",video.getGenres());data.put("from",before);data.put("to",after);data.put("createdAt",FieldValue.serverTimestamp());tx.set(event,data);return null;
  });
 }
 static Task<Void> view(Video video){
  String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();long day=System.currentTimeMillis()/86400000L;
  DocumentReference ref=FirebaseFirestore.getInstance().collection("viewEvents").document(uid+"_"+video.getId()+"_"+day);
  return FirebaseFirestore.getInstance().runTransaction(tx->{if(!tx.get(ref).exists()){Map<String,Object> data=new HashMap<>();data.put("uid",uid);data.put("videoId",video.getId());data.put("title",video.getTitle());data.put("genres",video.getGenres());data.put("day",day);data.put("createdAt",FieldValue.serverTimestamp());tx.set(ref,data);}return null;});
 }
}
