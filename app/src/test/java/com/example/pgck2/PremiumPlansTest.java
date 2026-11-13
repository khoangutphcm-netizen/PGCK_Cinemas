package com.example.pgck2;
import org.junit.Test;
import static org.junit.Assert.*;
import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
public class PremiumPlansTest {
 @Test public void expirationBoundary(){long now=100000;assertTrue(PremiumPlans.active(now,"month",now));assertFalse(PremiumPlans.active(now,"month",PremiumPlans.expires(now,"month")));assertFalse(PremiumPlans.active(now,"unknown",now));assertFalse(PremiumPlans.active(now,"year",now-1));assertEquals(365L*86400000,PremiumPlans.expires(0,"year"));}
 @Test public void realQrRoundTrip() throws Exception {String expected=PremiumPlans.qr("Abcd1234EFGH5678IJKL");BitMatrix matrix=new QRCodeWriter().encode(expected,BarcodeFormat.QR_CODE,640,640);int[] pixels=new int[640*640];for(int y=0;y<640;y++)for(int x=0;x<640;x++)pixels[y*640+x]=matrix.get(x,y)?0xff000000:0xffffffff;Result result=new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(640,640,pixels))));assertEquals(expected,result.getText());}
}
