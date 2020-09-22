package com.alexzamurca.animetrackersprint2.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;

import java.util.Calendar;

public class SessionAlarm
{
    private static final String TAG = "SetSessionAlarm";

    private Context mContext;

    public SessionAlarm(Context mContext)
    {
        this.mContext = mContext;
    }

    // session_duration = Session duration in millis
    public void setAlarm(int session_duration)
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, SessionExpiredReceiver.class);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, session_duration);

        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        Log.d(TAG, "setAlarm: alarm set for: " + convertDateToCalendar.reverseConvert(calendar));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void cancel()
    {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, SessionExpiredReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, intent, 0);
        alarmManager.cancel(pendingIntent);

        Log.d(TAG, "cancel: cancelled alarm");
    }
}
