package com.example.pgck2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;
final class ViewerLibrary {
 static CollectionReference collection(String name) {
  String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
  return FirebaseFirestore.getInstance().collection("users").document(uid).collection(name);
 }
 static String key(Video v){return v.getId();}
 static String historyKey(Video v,Episode e){return key(v)+"_"+e.getEpisodeNumber();}
 static Map<String,Object> entry(Video v){Map<String,Object> m=new HashMap<>();m.put("video",v);m.put("updatedAt",FieldValue.serverTimestamp());return m;}
 static boolean completed(long position,long duration){return duration>0 && position>=duration*0.9;}
 static com.google.android.gms.tasks.Task<Void> save(Video v,Episode e,long position,long duration){
  if(v==null||e==null||position<=0||duration<=0||FirebaseAuth.getInstance().getCurrentUser()==null)return com.google.android.gms.tasks.Tasks.forResult(null);
  Map<String,Object> m=entry(v);m.put("episodeNumber",e.getEpisodeNumber());m.put("position",position);m.put("duration",duration);m.put("completed",completed(position,duration));
  return collection("history").document(historyKey(v,e)).set(m);
 }
}
