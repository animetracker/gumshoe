package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.Calendar;

public class ResetAlarmForSeries
{
    private Context context;

    public ResetAlarmForSeries(Context context) {
        this.context = context;
    }

    public void reset(Series series)
    {
        // Cancel existing alarm
        NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
        notificationAiringChannel.cancel(series);

        // Set new alarm
        AdjustAirDate adjustAirDate = new AdjustAirDate(series);
        Calendar calendar = adjustAirDate.getCalendar();

        // If calendar returned it means all is good and notifications can be set
        if(calendar!=null)
        {
            notificationAiringChannel.setNotification(series, calendar);
        }
    }
}
