package com.akoudri.healthrecord.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ali Koudri on 20/11/14.
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static final String NOTIFICATION_ID = "notificationId";
    public static final String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        if (notificationId == 0) return;
        manager.notify(notificationId, notification);
    }
}
