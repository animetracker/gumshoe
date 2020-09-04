package com.alexzamurca.animetrackersprint2.notifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import static com.alexzamurca.animetrackersprint2.App.SERIES_AIRING_REMINDER_ID;

// This class monitors what happens when the air date of a series is now
// Intent carries the Series info
public class SeriesAiringNotificationReceiver extends BroadcastReceiver
{
    private static final String TAG = "SeriesAiringNotificationReceiver";

    private Context mContext;
    private NotificationManagerCompat notificationManagerCompat;
    private Series series;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        mContext = context;
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
                Notification notification = constructNotification();
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

    private Notification constructNotification()
    {
        Log.d(TAG, "constructNotification: constructing");
        // format: One Piece Episode 997 is airing in 30 min
        String text="";
        if(series.getNotification_change().equals(""))
        {
            text = "Episode " +
                    series.getEpisode_number() +
                    // Need a class to convert alert change to string if before ("is going to air in {time}"), if no change ("has just been released"), if after ("has released {time} ago")
                    " has just been released";
        }
        else if(series.getNotification_change().contains("before"))
        {
            // Get text before "before"
            String beforeText = series.getNotification_change().split(" before")[0];
            text = "Episode " +
                    series.getEpisode_number() +
                    " will be released in " + beforeText;
        }
        else if(series.getNotification_change().contains("after"))
        {
            // Get text before "after"
            String afterText = series.getNotification_change().split(" after")[0];
            text = "Episode " +
                    series.getEpisode_number() +
                    " was released " + afterText + " ago!";
        }


        return new  NotificationCompat.Builder(mContext, SERIES_AIRING_REMINDER_ID)
                // SHOW PROFILE ICON
                .setSmallIcon(R.drawable.ic_sun)
                .setContentTitle(series.getTitle())
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();
    }

    private void showNotification(Notification notification)
    {
        Log.d(TAG, "showNotification: showing notification");
        notificationManagerCompat.notify(series.getAnilist_id(), notification);
    }
}
