package com.example.pgck2;
import org.junit.Test;
import static org.junit.Assert.*;
public class ViewerLibraryTest {
 @Test public void completionRequiresKnownDuration(){assertFalse(ViewerLibrary.completed(100,0));assertFalse(ViewerLibrary.completed(100,-1));}
 @Test public void completionBoundary(){assertFalse(ViewerLibrary.completed(899,1000));assertTrue(ViewerLibrary.completed(900,1000));assertTrue(ViewerLibrary.completed(1000,1000));}
 @Test public void historySeparatesEpisodes(){Video v=new Video();v.setId("movie");assertNotEquals(ViewerLibrary.historyKey(v,new Episode(1,"","")),ViewerLibrary.historyKey(v,new Episode(2,"","")));}
}
