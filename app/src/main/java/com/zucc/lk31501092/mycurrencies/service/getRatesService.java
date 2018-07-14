package com.zucc.lk31501092.mycurrencies.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class getRatesService extends JobIntentService {
    public static final String URL_BASE = "http://openexchangerates.org/api/latest.json?app_id=";
    static final int JOB_ID = 10111;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork( context, getRatesService.class, JOB_ID, work );
    }

    @Override
    public void onHandleWork(@NonNull Intent intent) {
//        new Thread( new Runnable() {
//            @Override
//            public void run() {
//                Log.d( "TAG", "打印时间: " + new Date().toString() );
////                stopSelf();
//            }
//        } ).start();
        new getRatesTask().execute( URL_BASE + getKey() );
        AlarmManager manager = (AlarmManager) getSystemService( ALARM_SERVICE );
        int intervalTime = 60000 * 60;
        long triggerAtTime = SystemClock.elapsedRealtime() + intervalTime;
        Intent i = new Intent( this, AlarmReceiver.class );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( this, 0, i, 0 );
        manager.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent );
    }

    private String getKey() {
        AssetManager assetManager = this.getResources().getAssets();
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open( "keys.properties" );
            properties.load( inputStream );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty( "open_key" );
    }

}
