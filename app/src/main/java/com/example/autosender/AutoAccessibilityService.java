package com.example.autosender;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;

public class AutoAccessibilityService extends AccessibilityService {
    private WindowManager windowManager;
    private View floatingView;
    private boolean isSpamming = false;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        showFloatingWindow();
    }

    private void showFloatingWindow() {
        if (floatingView != null) return;

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null);
        
        int LAYOUT_FLAG;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        windowManager.addView(floatingView, params);

        EditText etText = floatingView.findViewById(R.id.etText);
        EditText etCount = floatingView.findViewById(R.id.etCount);
        Button btnStart = floatingView.findViewById(R.id.btnStart);
        Button btnStop = floatingView.findViewById(R.id.btnStop);

        btnStart.setOnClickListener(v -> {
            String text = etText.getText().toString();
            int count = 10;
            try {
                count = Integer.parseInt(etCount.getText().toString());
            } catch (Exception e) {}
            
            if (!text.isEmpty()) {
                startSpam(text, count);
            }
        });

        btnStop.setOnClickListener(v -> {
            isSpamming = false;
        });
    }

    private void startSpam(String text, int count) {
        if (isSpamming) return;
        isSpamming = true;

        new Thread(() -> {
            int sent = 0;
            while (isSpamming && sent < count) {
                try {
                    AccessibilityNodeInfo root = getRootInActiveWindow();
                    if (root != null) {
                        // البحث عن حقل الإدخال في واتساب
                        List<AccessibilityNodeInfo> inputs = root.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry");
                        if (!inputs.isEmpty()) {
                            AccessibilityNodeInfo inputNode = inputs.get(0);
                            Bundle args = new Bundle();
                            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
                            inputNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
                            
                            // البحث عن زرار الإرسال
                            List<AccessibilityNodeInfo> sends = root.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send");
                            if (!sends.isEmpty()) {
                                sends.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                            sent++;
                        }
                    }
                    // السرعة القصوى (بدون انتظار طويل، بس ممكن نضيف 10 مللي ثانية عشان النظام ميهنجش)
                    Thread.sleep(10); 
                } catch (Exception e) {
                    break;
                }
            }
            isSpamming = false;
        }).start();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override
    public void onInterrupt() {}
}
