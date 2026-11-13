package com.example.pgck2;
import java.text.Normalizer;
import java.util.Locale;
final class CatalogSearch {
    static String normalize(String value){return value==null?"":Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT),Normalizer.Form.NFD).replaceAll("\\p{M}+","").replace('đ','d');}
    static boolean matches(String query,String... values){String needle=normalize(query);if(needle.isEmpty())return true;for(String value:values)if(normalize(value).contains(needle))return true;return false;}
}
