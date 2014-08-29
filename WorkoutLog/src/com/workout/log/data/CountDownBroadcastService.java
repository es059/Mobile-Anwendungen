package com.workout.log.data;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

public class CountDownBroadcastService extends Service {    
	public static final String COUNTDOWN_BR = "com.workout.log.countdown_br";
    private Intent bi = new Intent(COUNTDOWN_BR);

    private CountDownTimer cdt = null;
    private long timerCount = 0;

    @Override
    public void onCreate() {       
        super.onCreate();
    }
    
    private void startTimer(long time){
    	cdt = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bi.putExtra("countdown", millisUntilFinished / 1000);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
            	bi.putExtra("countdown", -1);
                sendBroadcast(bi);
            }
        };
        cdt.start();
    }
    
    @Override
    public void onDestroy() {
        if (cdt != null) cdt.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {     
    	timerCount = intent.getIntExtra("Time", -1);
		if (timerCount != -1) startTimer(timerCount);
		
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {       
        return null;
    }
}