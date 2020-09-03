package com.alexzamurca.animetrackersprint2.notifications;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    // Storing the calendar for the air date and the air date after changes
    private Calendar airDateCalendar;
    private Calendar airDateAfterChangesCalendar;

    // Variable storing if airDate is before airDateAfterChanges
    private boolean airDateFirst;
    // Variable storing if airDate is the same as airDateAfterChanges
    private boolean calendarsEqual;

    public NotificationAiringChannel(Context context, Series series, Calendar airDateAfterChangesCalendar)
    {
        mContext = context;
        this.series = series;
        constructUpdateCalendar();
        this.airDateAfterChangesCalendar = airDateAfterChangesCalendar;
        compareCalendars();
    }

    private void constructUpdateCalendar()
    {
        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        Calendar calendar = convertDateToCalendar.convert(series.getAir_date());
        calendar.add(Calendar.MINUTE, 30);
        this.airDateCalendar = calendar;
    }

    private void compareCalendars()
    {
        if(airDateCalendar.before(airDateAfterChangesCalendar))
        {
            airDateFirst = true;
            calendarsEqual = false;
        }
        else if (airDateCalendar.equals(airDateAfterChangesCalendar))
        {
            airDateFirst = false;
            calendarsEqual = true;
        }
        else if(airDateCalendar.after(airDateAfterChangesCalendar))
        {
            airDateFirst = false;
            calendarsEqual = false;
        }
    }

    // Will happen at login and at additions to list
    public void setNotification(UpdateSeriesReceiver.OnAirDateListener onAirDateListener)
    {
        if(airDateAfterChangesCalendar != null)
        {
            startNotificationAlarm(onAirDateListener);
            setUpdateAlarm(onAirDateListener);
        }
        else
        {
            Log.d(TAG, "setNotification: calendar date is null");
        }
    }

    private void startNotificationAlarm(UpdateSeriesReceiver.OnAirDateListener onAirDateListener)
    {
        Log.d(TAG, "startAlarm: alarm started");
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, SeriesAiringNotificationReceiver.class);

        // Need to add extras to send Series object (used to construct the notification)
        Bundle args = new Bundle();
        args.putSerializable("series", series);
        args.putSerializable("onAirDateListener", onAirDateListener);
        args.putBoolean("set_new_notification", (airDateFirst&&!calendarsEqual)||(!airDateFirst&&calendarsEqual) );
        intent.putExtra("args", args);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, series.getAnilist_id(), intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, airDateAfterChangesCalendar.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "startAlarm: send series:" + series.getTitle());
    }

    private void setUpdateAlarm(UpdateSeriesReceiver.OnAirDateListener onAirDateListener)
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, UpdateSeriesReceiver.class);

        // Need to add extras to send Series object (used to construct the notification)
        Bundle args = new Bundle();
        args.putSerializable("series", series);
        args.putSerializable("onAirDateListener", onAirDateListener);
        args.putBoolean("set_new_notification", (!airDateFirst && !calendarsEqual));
        intent.putExtra("args", args);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, -series.getAnilist_id(), intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, airDateCalendar.getTimeInMillis(), pendingIntent);
    }

    // Will happen at log out, turning notifications off or changing air_date_change and notification_change
    public void cancel()
    {
        Log.d(TAG, "startAlarm: alarms cancelled");
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, SeriesAiringNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, series.getAnilist_id(), intent, 0);
        alarmManager.cancel(pendingIntent);

        Intent finishedIntent = new Intent(mContext, UpdateSeriesReceiver.class);
        PendingIntent finishedPendingIntent = PendingIntent.getBroadcast(mContext, -series.getAnilist_id(), finishedIntent, 0);
        alarmManager.cancel(finishedPendingIntent);
    }
}
