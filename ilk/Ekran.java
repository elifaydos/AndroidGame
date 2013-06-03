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

    
