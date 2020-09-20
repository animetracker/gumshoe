package com.alexzamurca.animetrackersprint2.notifications;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;

import java.util.Calendar;
import java.util.TimeZone;

// This class is for setting or cancelling notifications through the airing channel
public class UpdatingDBChannel
{
    private static final String TAG = "UpdatingDBChannel";

    // We need context to access Alarm manager (used to set and cancel alarms)
    private transient Context mContext;


    public UpdatingDBChannel(Context context)
    {
        mContext = context;
    }

    // Will happen at login and at additions to list
    // Daily at 4am
    public void startAlarmAt4()
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, UpdateDBReceiver.class);

        Calendar calendar = setUpCalendar();

        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        Log.d(TAG, "startAlarmAt4: alarm set for " + convertDateToCalendar.reverseConvert(calendar));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void startAlarm1HourLater()
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, UpdateDBReceiver.class);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.add(Calendar.HOUR, 1);

        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        Log.d(TAG, "startAlarm1HourLater: alarm set for " + convertDateToCalendar.reverseConvert(calendar));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    // Will happen at log out, turning notifications off or changing air_date_change and notification_change
    public void cancel()
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, UpdateDBReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "cancel: update DB alarm cancelled");
    }

    private Calendar setUpCalendar()
    {
        Calendar calendar = Calendar.getInstance();

        // If its 4am or after 4am today set alarm for next day
        if(calendar.get(Calendar.HOUR_OF_DAY) >= 4)
        {
            Log.d(TAG, "setUpCalendar: update DB alarm set for next day");
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 4);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }
}
