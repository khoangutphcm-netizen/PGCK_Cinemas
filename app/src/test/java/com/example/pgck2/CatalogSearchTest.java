package com.example.pgck2;
import org.junit.Test;
import static org.junit.Assert.*;
public class CatalogSearchTest {
    @Test public void vietnameseSearchIgnoresAccentsAndCase(){assertTrue(CatalogSearch.matches("  dien anh ","ĐIỆN ẢNH"));assertTrue(CatalogSearch.matches("hanh dong","Phim Hành Động"));assertTrue(CatalogSearch.matches("điện","Dien anh"));}
    @Test public void nullsAndEmptyQueriesAreSafe(){assertTrue(CatalogSearch.matches("",(String)null));assertFalse(CatalogSearch.matches("phim",(String)null));assertTrue(CatalogSearch.matches(null,"Tiêu đề"));}
    @Test public void searchesAllFields(){assertTrue(CatalogSearch.matches("admin@", "Tên người dùng", "admin@gmail.com"));assertFalse(CatalogSearch.matches("kinh di", "Hài hước", "Hành động"));}
}
