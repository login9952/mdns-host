package com.example.mdnshost;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class MdnsForegroundService extends Service {

    private MdnsManager mdnsManager;
    private static final String CHANNEL_ID = "mdns_service";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean started = false;

    private final Runnable wifiChecker = new Runnable() {
        @Override
        public void run() {
            if (isWifiConnected()) {
                startMdns();
            } else {
                handler.postDelayed(this, 3000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("mDNS Host")
                        .setContentText("等待 WiFi")
                        .setSmallIcon(android.R.drawable.ic_menu_info_details)
                        .build();

        startForeground(1, notification);

        handler.post(wifiChecker);
    }

    private void startMdns() {
        if (started) {
            return;
        }

        started = true;

        mdnsManager = new MdnsManager(this);
        mdnsManager.start();

        System.out.println("MDNS_SERVICE_STARTED");
    }

    private boolean isWifiConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        android.net.Network network = cm.getActiveNetwork();
        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities =
                cm.getNetworkCapabilities(network);

        return capabilities != null
                && capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void createNotificationChannel() {

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "mDNS 服务",
                        NotificationManager.IMPORTANCE_LOW
                );

        NotificationManager manager =
                getSystemService(NotificationManager.class);

        manager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {

        handler.removeCallbacks(wifiChecker);

        if (mdnsManager != null) {
            mdnsManager.stop();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
