package com.example.zhuzh.a6200finalwithfirebase.ServicePackage;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class TimerService extends Service {
    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CountDownTimer countDownTimer = new CountDownTimer(1000 * 60 * 60,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("Timer", millisUntilFinished+"");
            }

            @Override
            public void onFinish() {
                Log.e("Finish","Finish");

            }
        }.start();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopSelf();
        return START_STICKY;

//        return Service.START_STICKY_COMPATIBILITY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
