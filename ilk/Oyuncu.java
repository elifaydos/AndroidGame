package com.android.TuglaOyunu;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Oyuncu {

    private float x, y;

    private float yukseklik;
    private float genislik;

    private Bitmap bitmap;

    private boolean hayatta;
    private int kalanHaklar;
    
    public Oyuncu(Resources res) {
        bitmap = BitmapFactory.decodeResource(res, R.drawable.tahta);
        yukseklik = bitmap.getHeight();
        genislik = bitmap.getWidth();
        x = (Ekran.genislik / 2) - (genislik / 2);
        y = Ekran.yukseklik - 50;
        
        hayatta = true;
        kalanHaklar = 20;
    }
    
    public void doDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public void animate(int oryantasyon) {
    	if (oryantasyon < 0) {
    		x --;
    	}
    	else if (oryantasyon > 0) {
    		x ++;
    	}
    	if (x < 0) {
    		x = 0;
    	}
    	if (x + genislik > Ekran.genislik) {
    		x = Ekran.genislik - genislik;
    	}
    		
    }
    
    public void birHakKaybi() {
    	if (kalanHaklar-- == 0) {
    		oldur();
    	}
        x = (Ekran.genislik / 2) - (genislik / 2);
        y = Ekran.yukseklik - 50;
    }

    public boolean hayattami() {
    	return hayatta;
    }

    public void oldur() {
    	hayatta = false;
    }
    
    public int getKalanHaklar() {
    	return kalanHaklar;
    }
    
    public float getGenislik(){
    	return genislik;
    }

    public float getYukseklik(){
    	return yukseklik;
    }

    public float getX() {
    	return x;
    }
    
    public float getY() {
    	return y;
    }
}
