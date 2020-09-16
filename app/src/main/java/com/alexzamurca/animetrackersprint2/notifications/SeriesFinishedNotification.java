package com.alexzamurca.animetrackersprint2.notifications;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import static com.alexzamurca.animetrackersprint2.App.SERIES_AIRING_REMINDER_ID;

public class SeriesFinishedNotification
{
    private static final String TAG = "SeriesFinishedNotification";

    private Context mContext;
    private NotificationManagerCompat notificationManagerCompat;
    private Series series;
    private String newStatus;

    public SeriesFinishedNotification(Context context, Series series, String newStatus)
    {
        mContext = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        this.series = series;
        this.newStatus = newStatus;
    }

    private Notification constructNotification()
    {
        Log.d(TAG, "constructNotification: constructing");
        // format: One Piece Episode 997 is airing in 30 min
        String text="Series has now been removed from your list!";
        if(newStatus.equals("FINISHED"))
        {
            text = "Series has now been removed from your list as it has finished!";
        }
        else if(newStatus.equals("CANCELLED"))
        {
            text = "Series has now been removed from your list as it has been cancelled!";
        }

        return new  NotificationCompat.Builder(mContext, SERIES_AIRING_REMINDER_ID)
                .setSmallIcon(R.drawable.ic_gumshoe_notification_fill_icon)
                .setColor(ContextCompat.getColor(mContext, R.color.pleasantBlue))
                .setContentTitle(series.getTitle())
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();
    }

    public void showNotification()
    {
        Notification notification = constructNotification();
        Log.d(TAG, "showNotification: showing notification");
        notificationManagerCompat.notify(-series.getAnilist_id(), notification);
    }
}
