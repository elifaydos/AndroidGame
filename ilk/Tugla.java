package com.android.TuglaOyunu;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Tugla {
    private float x, y;
    
    private static float yukseklik = 0;
    private static float genislik = 0;
    
    private boolean canli;
    
    private static Bitmap bitmap;
    
    public Tugla(Resources res, int ilkX, int ilkY) {
        bitmap = BitmapFactory.decodeResource(res, R.drawable.tugla);
        x = ilkX;
        y = ilkY;
        canli = true;
    }
    
    public void doDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, null, new Rect((int)x, (int)y, (int)(x+genislik), (int)(y+yukseklik)), null);
    }
    
    public void vuruldu() {
    	canli = false;
    }

    public boolean canlimi() {
    	return canli;
    }

    public static void setGenislik(float width){
    	genislik = width;
    }
    
    public static void setYukseklik(float height){
    	yukseklik = height;
    }
    
    public static float getGenislik(){
    	return genislik;
    }

    public static float getYukseklik(){
    	return yukseklik;
    }

    public float getX() {
    	return x;
    }
    
    public float getY() {
    	return y;
    }
}
