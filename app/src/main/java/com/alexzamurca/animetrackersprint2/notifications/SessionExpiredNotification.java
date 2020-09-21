package com.alexzamurca.animetrackersprint2.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.alexzamurca.animetrackersprint2.MainActivity;
import com.alexzamurca.animetrackersprint2.R;

import static com.alexzamurca.animetrackersprint2.App.SERIES_AIRING_REMINDER_ID;
import static com.alexzamurca.animetrackersprint2.App.SETTINGS_ID;

public class SessionExpiredNotification
{
    private static final String TAG = "SessionExpiredNotificat";

    private Context mContext;
    private NotificationManagerCompat notificationManagerCompat;

    public SessionExpiredNotification(Context context)
    {
        mContext = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
    }

    private Notification constructNotification()
    {
        Log.d(TAG, "constructNotification: constructing");
        String title="Your session has expired";
        String text = "Re-login to continue using the app :)";

        // Intent to reopen app on notification click
        Intent activityIntent = new Intent(mContext, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        return new  NotificationCompat.Builder(mContext, SETTINGS_ID)
                .setSmallIcon(R.drawable.ic_gumshoe_notification_fill_icon)
                .setColor(ContextCompat.getColor(mContext, R.color.pleasantRed))
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .build();
    }

    public void showNotification()
    {
        Notification notification = constructNotification();
        Log.d(TAG, "showNotification: showing notification");
        notificationManagerCompat.notify(1, notification);
    }
}
