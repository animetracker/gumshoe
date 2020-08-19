package com.alexzamurca.animetrackersprint2.notifications;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.Calendar;

// This class is for setting or cancelling notifications through the airing channel
public class NotificationAiringChannel
{
    private static final String TAG = "NotificationAiringChannel";
    // We need the series info to send it to the receiver
    private Series series;

    // We need context to access Alarm manager (used to set and cancel alarms)
    private Context mContext;

    public NotificationAiringChannel(Context context, Series series)
    {
        mContext = context;
        this.series = series;
    }

    // Will happen at login and at additions to list
    public void setNotification()
    {
        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar(series.getAir_date());
        Calendar calendarAirTime  = convertDateToCalendar.convert();
        if(calendarAirTime != null)
        {
            startAlarm(calendarAirTime);
        }
        else
        {
            Log.d(TAG, "setNotification: couldn't convert anime string airing date to calendar date");
        }
    }

    private void startAlarm(Calendar calendar)
    {
        Log.d(TAG, "startAlarm: alarm started");
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, SeriesAiringNotificationReceiver.class);
        // Need to add extras to send Series object (used to construct the notification)
        intent.putExtra("series", series);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    // Will happen at log out
    public void cancel()
    {
        Log.d(TAG, "startAlarm: alarm cancelled");
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, SeriesAiringNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}
