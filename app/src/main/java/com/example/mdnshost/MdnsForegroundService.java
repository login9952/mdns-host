package com.example.mdnshost;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class MdnsForegroundService extends Service {

    private static final String CHANNEL_ID = "mdns_service";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("mDNS Host")
                        .setContentText("正在运行")
                        .setSmallIcon(android.R.drawable.ic_menu_info_details)
                        .build();

        startForeground(1, notification);
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
    public IBinder onBind(Intent intent) {
        return null;
    }
}
