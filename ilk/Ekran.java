package com.android.TuglaOyunu;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Ekran extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint = new Paint();
    
    public static float genislik; //ekran genisligi
    public static float yukseklik; //ekran yuksekligi

  private SensorManager sensorYoneticisi;
	private Sensor oryantasyonSensoru;
	private int oryantasyon;
	private MediaPlayer mpTahta;
	private MediaPlayer mpTugla;
	
    private int sutunSayisi = 9;
    private int satirSayisi = 3;
    
    private GorunumThread gorunumThread;
    private Oyuncu oyuncu; 
    private Top top;
    private ArrayList<Tugla> tuglaDizisi = new ArrayList<Tugla>();

    private int vurulanTuglaSayisi = 0;
    
    private boolean ilklemeTamamlandi = false;
    
    public Ekran(Context context) {
    	super(context);
        getHolder().addCallback(this);
        gorunumThread = new GorunumThread(this);
        paint.setColor(Color.WHITE);
        
        sensorYoneticisi = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        oryantasyonSensoru = sensorYoneticisi.getDefaultSensor(Sensor.TYPE_ORIENTATION);

    	sensorYoneticisi.registerListener(sensorDinleyici, oryantasyonSensoru, SensorManager.SENSOR_DELAY_GAME);
    	
		mpTahta = MediaPlayer.create(context, R.raw.tahtaya_carpma_sesi);
		mpTugla = MediaPlayer.create(context, R.raw.tuglaya_carpma_sesi);
    }

	private SensorEventListener sensorDinleyici = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			oryantasyon = (int)event.values[2];
			Log.i("Orientation changed!!", "Orientation = " + oryantasyon);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {		
		}
	};

    public void surfaceCreated(SurfaceHolder holder) {
        if (!gorunumThread.isAlive()) {
            gorunumThread.calismaDurumuAyarla(true);
            gorunumThread.start();
        }
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int yeniGenislik, int yeniYukseklik) {
        genislik = yeniGenislik;
        yukseklik = yeniYukseklik;
        
        oyuncu = new Oyuncu(getResources());
        top = new Top(getResources(), (int) (oyuncu.getX() + (oyuncu.getGenislik() / 2)), (int) oyuncu.getY());
        top.setY(oyuncu.getY() - top.getYukseklik());
        
        Tugla.setGenislik(genislik / sutunSayisi);
        Tugla.setYukseklik((yukseklik / 3) / satirSayisi); //ekranin duseyde ucte birini kaplasin

        for (int i=0; i<satirSayisi; i++) {
        	for (int j=0; j<sutunSayisi; j++) {
        		tuglaDizisi.add(new Tugla(getResources(), (int) (j*Tugla.getGenislik()), (int) (i*Tugla.getYukseklik())));
        	}
        }
        ilklemeTamamlandi = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (gorunumThread.isAlive()) {
            gorunumThread.calismaDurumuAyarla(false);
        }
        if (sensorYoneticisi != null) {
        	sensorYoneticisi.unregisterListener(sensorDinleyici);
        }
        if (mpTahta != null)
        	mpTahta.release();
        if (mpTugla != null)
        	mpTugla.release();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	top.kostur();
        return super.onTouchEvent(event);
    }

    public void doDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        
        if (oyuncu == null) 
        	return;
        
        if (oyuncu.hayattami() == true) {
	        if (ilklemeTamamlandi)
	        {
	        	oyuncu.doDraw(canvas);
		        synchronized (tuglaDizisi) {
		            for (Tugla tugla : tuglaDizisi) {
		            	if (tugla.canlimi() == true)
		            		tugla.doDraw(canvas);
		            }
		        }
	        }
	
        	if (top != null)
        		top.doDraw(canvas);
	
		    canvas.drawText("Skor: " + vurulanTuglaSayisi, 25, yukseklik - 15, paint);
		    canvas.drawText("Kalan Haklar: "+ oyuncu.getKalanHaklar(), genislik - 80, yukseklik - 15, paint);
        }
        else {
        	paint.setColor(Color.CYAN);
        	paint.setTextSize(25);
        	
		    if (vurulanTuglaSayisi == (satirSayisi * sutunSayisi))
		    	canvas.drawText("TEBRIKLER, KAZANDINIZ", 15, yukseklik / 2, paint);
		    else
		    	canvas.drawText("UZGUNUM, KAYBETTINIZ", 15, yukseklik / 2, paint);
        }
    }
    
    public void animate(long gecenSure) {
        if (top != null)
        {
        	if (oyuncu.hayattami() == true) {
        		oyuncu.animate(oryantasyon * (-1));
        		if (top.hareketlimi() == true)
        			top.animate(gecenSure);
        		else
        			top.setX(oyuncu.getX() + (oyuncu.getGenislik() / 2));
        	}
            
        	if (top.hareketlimi() == true) {
	        	if ((top.getY() + top.getYukseklik()) > (oyuncu.getY())) {
	            	if (((top.getX() + top.getGenislik())  > (oyuncu.getX())) && 
	                	((top.getX())                    < (oyuncu.getX() + oyuncu.getGenislik())))
	            	{
	        			top.setHizY(-1*top.getHizY());
	        			top.setY(oyuncu.getY() - top.getYukseklik());
	        	        if (mpTahta != null)
	        	        	mpTahta.start();
	            	}
	            	else
	            	{
	            		oyuncu.birHakKaybi();
	            		top.durdur();
	            		top.setX(oyuncu.getX() + (oyuncu.getGenislik() / 2));
	            		top.setY(oyuncu.getY() - top.getYukseklik());
	                    top.setHizX(Top.ilkHizX);
	                    top.setHizY(Top.ilkHizY);
	            	}
	        	}
        	}

	        synchronized (tuglaDizisi) {
	            for (Tugla tugla : tuglaDizisi) {
	            	if (tugla.canlimi() == true) {
	            		if (((top.getX() + top.getGenislik())  > (tugla.getX())) && 
	            			((top.getX())                    < (tugla.getX() + Tugla.getGenislik())) &&
	            			((top.getY() + top.getYukseklik()) > (tugla.getY())) &&
	            			((top.getY())                    < (tugla.getY() + Tugla.getYukseklik())))
	            		{
	            			tugla.vuruldu();
	            			if (mpTugla != null)
	            				mpTugla.start();
	
	            			if (++vurulanTuglaSayisi == (sutunSayisi * satirSayisi))
	            			  oyuncu.oldur();
	            			
	            			float a,b,c,d, min;
	
	            			a = Math.abs((top.getX() + top.getGenislik())  -(tugla.getX()));
	            			b = Math.abs((top.getX())                    -(tugla.getX() + Tugla.getGenislik()));
	            			c = Math.abs((top.getY() + top.getYukseklik()) -(tugla.getY()));
	            			d = Math.abs((top.getY())                    -(tugla.getY() + Tugla.getYukseklik()));
	            			
	            			min = Math.min(Math.min(a, b), Math.min(c, d));
	            			if (min == a) {
	            				top.setHizX(-1*top.getHizX());
	            				top.setX(tugla.getX() - top.getGenislik());
	            			}
	            			else if (min == b) {
	            				top.setHizX(-1*top.getHizX());
	            				top.setX(tugla.getX() + Tugla.getGenislik());
	            			}
	            			else if (min == c) {
	            				top.setHizY(-1*top.getHizY());
	            				top.setY(tugla.getY() - top.getYukseklik());
	            			}
	            			else if (min == d) {
	            				top.setHizY(-1*top.getHizY());
	            				top.setY(tugla.getY() + Tugla.getYukseklik());
	            			}
	
	            			break;
	            		}
	            	}
	            }
	        }
        }
    }
}
