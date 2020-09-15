package com.alexzamurca.animetrackersprint2.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.alexzamurca.animetrackersprint2.MainActivity;
import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import static com.alexzamurca.animetrackersprint2.App.SERIES_AIRING_REMINDER_ID;

// This class monitors what happens when the air date of a series is now
// Intent carries the Series info
public class SeriesAiringNotificationReceiver extends BroadcastReceiver
{
    private static final String TAG = "SeriesAiringNotificationReceiver";

    private NotificationManagerCompat notificationManagerCompat;
    private Series series;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        notificationManagerCompat = NotificationManagerCompat.from(context);

        Bundle args = intent.getBundleExtra("args");
        if(args!=null)
        {
            // getting set_new_notification boolean
            boolean set_new_notification = args.getBoolean("set_new_notification");

            Log.d(TAG, "onReceive: bundle received");
            series = (Series) args.getSerializable("series");
            if(series!=null)
            {
                Log.d(TAG, "onReceive: series received:" + series.getTitle());

                // Construct and show notification
                Notification notification = constructNotification(context);
                showNotification(notification);

                Log.d(TAG, "onReceive: setNewNotification:" +  set_new_notification);
                // Setting new Notification
                if(set_new_notification)
                {

                    setNewNotification(context, series);
                }

            }
        }
    }

    private void setNewNotification(Context context, Series series)
    {
        SetNewNotification setNewNotification = new SetNewNotification(context, series);
        setNewNotification.setNotification();
    }

    private Notification constructNotification(Context context)
    {
        Log.d(TAG, "constructNotification: constructing");
        // format: One Piece Episode 997 is airing in 30 min
        String text="";
        if(series.getNotification_change().equals(""))
        {
            text = "Episode " +
                    series.getNext_episode_number() +
                    // Need a class to convert alert change to string if before ("is going to air in {time}"), if no change ("has just been released"), if after ("has released {time} ago")
                    " has just been released";
        }
        else if(series.getNotification_change().contains("before"))
        {
            // Get text before "before"
            String beforeText = series.getNotification_change().split(" before")[0];
            text = "Episode " +
                    series.getNext_episode_number() +
                    " will be released in " + beforeText;
        }
        else if(series.getNotification_change().contains("after"))
        {
            // Get text before "after"
            String afterText = series.getNotification_change().split(" after")[0];
            text = "Episode " +
                    series.getNext_episode_number() +
                    " was released " + afterText + " ago!";
        }

        // Intent to reopen app on notification click
        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notificationsOffIntent = new Intent(context, MainActivity.class);
        notificationsOffIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle =  new Bundle();
        bundle.putBoolean("notifications_off", true);
        bundle.putSerializable("series", series);
        notificationsOffIntent.putExtra("bundle_notifications_off", bundle);
        PendingIntent notificationsOffActionIntent = PendingIntent.getActivity(context, 2, notificationsOffIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent incorrectAirDateIntent = new Intent(context, MainActivity.class);
        incorrectAirDateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle args =  new Bundle();
        bundle.putBoolean("incorrect_air_date", true);
        bundle.putSerializable("series", series);
        notificationsOffIntent.putExtra("bundle_incorrect_air_date", args);
        PendingIntent incorrectAirDateActionIntent = PendingIntent.getActivity(context, -2, incorrectAirDateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new  NotificationCompat.Builder(context, SERIES_AIRING_REMINDER_ID)
                .setSmallIcon(R.drawable.ic_gumshoe_notification_fill_icon)
                .setColor(ContextCompat.getColor(context, R.color.pleasantBlue))
                .setContentTitle(series.getTitle())
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .addAction(R.drawable.ic_notifications_on, "Turn notifications Off", notificationsOffActionIntent)
                .addAction(R.drawable.ic_error_black, "Incorrect Air Date", incorrectAirDateActionIntent)
                .build();
    }

    private void showNotification(Notification notification)
    {
        Log.d(TAG, "showNotification: showing notification");
        notificationManagerCompat.notify(series.getAnilist_id(), notification);
    }
}
