package com.alexzamurca.animetrackersprint2.notifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.Objects;

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

        series = (Series) Objects.requireNonNull(intent.getExtras()).getSerializable("series");

        // Construct and show notification
        Notification notification = constructNotification();
        showNotification(notification);

        // Then update series in recyclerView list and DB
    }

    private Notification constructNotification()
    {
        Log.d(TAG, "constructNotification: constructing");
        // format: One Piece Episode 997 is airing in 30 min

        String text = series.getTitle() +
                " Episode " +
                series.getEpisode_number() +
                // Need a class to convert alert change to string if before ("is going to air in {time}"), if no change ("has just been released"), if after ("has released {time} ago")
                " has just been released";

        return new  NotificationCompat.Builder(mContext, SERIES_AIRING_REMINDER_ID)
                .setSmallIcon(R.drawable.ic_sun)
                .setContentTitle("Series Airing Reminder")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();
    }

    private void showNotification(Notification notification)
    {
        Log.d(TAG, "showNotification: showing notification");
        notificationManagerCompat.notify(series.getAnilist_id(), notification);
    }
}
