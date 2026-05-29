package com.jaystings.immutablealarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;

public class TimerService extends Service {
    private final IBinder binder = new LocalBinder();
    private PowerManager.WakeLock wakeLock;
    private final Handler handler = new Handler();
    private Runnable activeRunnable;
    private static final String CHANNEL_ID = "ModularServiceChannel";

    public class LocalBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "App::ModularWakeLock");
            wakeLock.acquire();
        }
    }

    // Call this from your Activity to pass and start your Runnable
    public void startTask(Runnable runnable) {
        stopTask(); // Clear any previous task
        this.activeRunnable = runnable;
        handler.post(activeRunnable);

        // Promote to foreground service to prevent OS shutdown
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Task Running")
                .setContentText("Your custom task is active in the background.")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build();
        startForeground(1, notification);
    }

    public void stopTask() {
        if (activeRunnable != null) {
            handler.removeCallbacks(activeRunnable);
            activeRunnable = null;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Background Task", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        stopTask();
        if (wakeLock != null && wakeLock.isHeld()) wakeLock.release();
        super.onDestroy();
    }
}