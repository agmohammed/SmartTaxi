package com.agmohammed.smarttaxi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by aG on 9/18/2018.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();

        CreateNotification(context, "Get Ready!!!", "Your ride starts in 15 minutes", "Alert");



//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag");
//
////        Acquire the lock
//        wl.acquire();
//
//        Log.v("ADebugTag", "It work!");
//
////        Release the lock
//        wl.release();
    }

    private void CreateNotification(Context context, String msg, String msgText, String msgAlert) {

        PendingIntent notificIntent = PendingIntent.getActivity(context, 0, new Intent(context, DriverMapActivity.class),0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                . setSmallIcon(R.drawable.ic_alarm_on)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText);

        mBuilder.setContentIntent(notificIntent);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }
}
