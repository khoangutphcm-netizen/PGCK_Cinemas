package com.example.pgck2;
final class PremiumPlans {
 static int days(String plan){return "year".equals(plan)?365:"month".equals(plan)?30:0;}
 static long price(String plan){return "year".equals(plan)?399000:"month".equals(plan)?49000:0;}
 static long expires(long confirmedAt,String plan){return confirmedAt+days(plan)*86400000L;}
 static boolean active(long confirmedAt,String plan,long now){return days(plan)>0&&confirmedAt<=now&&expires(confirmedAt,plan)>now;}
 static String qr(String id){return "pcgkcinema://premium/order/"+id;}
}
