package com.example.pgck2;

import java.text.SimpleDateFormat;
import java.util.*;

/** Pure aggregation, independent of Android/Firebase so report boundaries can be tested. */
final class StatsReport {
    static final TimeZone ZONE=TimeZone.getTimeZone("Asia/Bangkok");
    static final String UNCATEGORIZED="Chưa phân loại / đã gỡ";
    static final class Movie {
        final String id,title;final List<String> genres;
        Movie(String id,String title,List<String> genres){this.id=id;this.title=title==null?"Phim chưa đặt tên":title;this.genres=categories(genres);}
    }
    static final class Event {
        final String movieId,title,uid,kind;final long time;final List<String> genres;
        Event(String movieId,String title,String uid,String kind,long time,List<String> genres){this.movieId=movieId;this.title=title;this.uid=uid;this.kind=kind;this.time=time;this.genres=categories(genres);}
    }
    static final class Numbers {
        long views,likes,dislikes,removals,activeLikes,activeDislikes,movies;
        long value(int metric){return metric==1?likes:metric==2?dislikes:metric==3?activeLikes:metric==4?activeDislikes:views;}
    }
    static final class Row {
        final String id,title;final boolean removed;final Numbers numbers=new Numbers();
        Row(String id,String title,boolean removed){this.id=id;this.title=title==null?"Phim đã gỡ":title;this.removed=removed;}
    }
    final Numbers total=new Numbers();
    final Map<String,Row> movies=new LinkedHashMap<>();
    final Map<String,Numbers> genres=new TreeMap<>(),timeline=new TreeMap<>();
    final Set<String> viewers=new HashSet<>();
    final int period;final long start,end;
    StatsReport(Map<String,Movie> catalog,Collection<String> genreNames,List<Event> viewEvents,List<Event> reactionEvents,List<Event> active,int period,int year,int month,String selected){
        this.period=period;long[] range=range(period,year,month);start=range[0];end=range[1];
        for(String name:genreNames)if(selected==null||selected.equals(name))genres.put(name,new Numbers());
        for(Movie movie:catalog.values()){
            if(!matches(movie.genres,selected))continue;
            Row row=new Row(movie.id,movie.title,false);row.numbers.movies=1;movies.put(movie.id,row);total.movies++;
            for(String g:groups(movie.genres,selected))genres.computeIfAbsent(g,k->new Numbers()).movies++;
        }
        if(period!=0){Calendar c=Calendar.getInstance(ZONE);c.setTimeInMillis(start);while(c.getTimeInMillis()<end){timeline.put(bucket(c.getTimeInMillis(),period),new Numbers());c.add(period==1?Calendar.DAY_OF_MONTH:Calendar.MONTH,1);}}
        for(Event event:viewEvents)add(event,catalog,selected,false);
        for(Event event:reactionEvents)add(event,catalog,selected,false);
        for(Event event:active)add(event,catalog,selected,true);
    }
    private void add(Event event,Map<String,Movie> catalog,String selected,boolean active){
        Movie movie=catalog.get(event.movieId);List<String> categories=movie==null?event.genres:movie.genres;
        if(!matches(categories,selected)||(!active&&(event.time<start||event.time>=end)))return;
        Row row=movies.computeIfAbsent(event.movieId,id->new Row(id,movie==null?event.title:movie.title,movie==null));
        update(total,event.kind,active);update(row.numbers,event.kind,active);
        for(String genre:groups(categories,selected))update(genres.computeIfAbsent(genre,k->new Numbers()),event.kind,active);
        if(!active){update(timeline.computeIfAbsent(bucket(event.time,period),k->new Numbers()),event.kind,false);if("view".equals(event.kind)&&event.uid!=null)viewers.add(event.uid);}
    }
    static void update(Numbers numbers,String kind,boolean active){
        if(active){if("like".equals(kind))numbers.activeLikes++;else if("dislike".equals(kind))numbers.activeDislikes++;return;}
        if("view".equals(kind))numbers.views++;else if("like".equals(kind))numbers.likes++;else if("dislike".equals(kind))numbers.dislikes++;else if("none".equals(kind))numbers.removals++;
    }
    List<Row> ranking(int metric,boolean ascending,boolean includeRemoved){
        List<Row> rows=new ArrayList<>();for(Row row:movies.values())if(includeRemoved||!row.removed)rows.add(row);
        rows.sort((a,b)->{int comparison=Long.compare(a.numbers.value(metric),b.numbers.value(metric));if(!ascending)comparison=-comparison;if(comparison==0)comparison=a.title.compareToIgnoreCase(b.title);return comparison==0?a.id.compareTo(b.id):comparison;});return rows;
    }
    static List<String> categories(List<String> values){Set<String> clean=new LinkedHashSet<>();if(values!=null)for(String value:values)if(value!=null&&!value.trim().isEmpty())clean.add(value.trim());if(clean.isEmpty())clean.add(UNCATEGORIZED);return new ArrayList<>(clean);}
    private static boolean matches(List<String> genres,String selected){return selected==null||genres.contains(selected);}
    private static List<String> groups(List<String> genres,String selected){return selected==null?genres:Collections.singletonList(selected);}
    static long[] range(int period,int year,int month){if(period==0)return new long[]{Long.MIN_VALUE,Long.MAX_VALUE};Calendar c=Calendar.getInstance(ZONE);c.clear();c.set(year,period==2?Calendar.JANUARY:month,1);long start=c.getTimeInMillis();c.add(period==2?Calendar.YEAR:Calendar.MONTH,1);return new long[]{start,c.getTimeInMillis()};}
    static String bucket(long time,int period){SimpleDateFormat format=new SimpleDateFormat(period==1?"yyyy-MM-dd":"yyyy-MM",Locale.ROOT);format.setTimeZone(ZONE);return format.format(new Date(time));}
}
