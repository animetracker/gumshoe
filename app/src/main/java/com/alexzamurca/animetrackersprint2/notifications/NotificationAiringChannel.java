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

    // We need context to access Alarm manager (used to set and cancel alarms)
    private transient Context mContext;

    // Storing the calendar for the air date and the air date after changes
    private Calendar airDateCalendar;
    private Calendar airDateAfterChangesCalendar;

    // Variable storing if airDate is before airDateAfterChanges
    private boolean airDateFirst;
    // Variable storing if airDate is the same as airDateAfterChanges
    private boolean calendarsEqual;

    public NotificationAiringChannel(Context context)
    {
        mContext = context;
    }

    private void constructUpdateCalendar(String air_date)
    {
        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        airDateCalendar = convertDateToCalendar.noTimeZoneConvert(air_date);
        //airDateCalendar = convertDateToCalendar.noTimeZoneConvert(series.getAir_date());
        //airDateCalendar.add(Calendar.MINUTE, 30);
        airDateCalendar.add(Calendar.SECOND, 1);
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
        Log.d(TAG, "compareCalendars: variables set; airDateFirst: " + airDateFirst + " , calendarsEqual: " + calendarsEqual);
    }

    // Will happen at login and at additions to list
    public void setNotification(Series series, Calendar airDateAfterChangesCalendar)
    {
        constructUpdateCalendar(series.getAir_date());
        this.airDateAfterChangesCalendar = airDateAfterChangesCalendar;
        compareCalendars();

        if(airDateAfterChangesCalendar != null)
        {
            setUpdateAlarm(series);
            startNotificationAlarm(series);
        }
        else
        {
            Log.d(TAG, "setNotification: calendar date is null");
        }
    }

    private void startNotificationAlarm(Series series)
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, SeriesAiringNotificationReceiver.class);

        // Need to add extras to send Series object (used to construct the notification)
        Bundle args = new Bundle();
        args.putSerializable("series", series);
        args.putBoolean("set_new_notification", (airDateFirst&&!calendarsEqual)||(!airDateFirst&&calendarsEqual) );
        intent.putExtra("args", args);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, series.getAnilist_id(), intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, airDateAfterChangesCalendar.getTimeInMillis(), pendingIntent);

        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        Log.d(TAG, "startNotificationAlarm: set notification alarm for \"" + series.getTitle() + "\" on date: " + convertDateToCalendar.reverseConvert(airDateAfterChangesCalendar));
    }

    private void setUpdateAlarm(Series series)
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, UpdateSeriesReceiver.class);

        // Need to add extras to send Series object (used to construct the notification)
        Bundle args = new Bundle();
        args.putSerializable("series", series);
        args.putBoolean("set_new_notification", (!airDateFirst && !calendarsEqual));
        intent.putExtra("args", args);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, -series.getAnilist_id(), intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, airDateCalendar.getTimeInMillis(), pendingIntent);

        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        Log.d(TAG, "startNotificationAlarm: set update series in DB alarm for \"" + series.getTitle() + "\" on date: " + convertDateToCalendar.reverseConvert(airDateCalendar));
    }

    // Will happen at log out, turning notifications off or changing air_date_change and notification_change
    public void cancel(Series series)
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, SeriesAiringNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, series.getAnilist_id(), intent, 0);
        alarmManager.cancel(pendingIntent);

        Intent finishedIntent = new Intent(mContext, UpdateSeriesReceiver.class);
        PendingIntent finishedPendingIntent = PendingIntent.getBroadcast(mContext, -series.getAnilist_id(), finishedIntent, 0);
        alarmManager.cancel(finishedPendingIntent);

        Log.d(TAG, "cancel: cancelled alarm for: " + series.getTitle());
    }
}
