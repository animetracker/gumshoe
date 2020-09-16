package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.notifications.UpdatingDBChannel;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.Calendar;

public class ResetAlarmForUpdateDB
{
    private Context context;

    public ResetAlarmForUpdateDB(Context context) {
        this.context = context;
    }

    public void reset()
    {
        // Cancel existing alarm
        UpdatingDBChannel updatingDBChannel = new UpdatingDBChannel(context);
        updatingDBChannel.cancel();

        updatingDBChannel.setNotification();
    }
}
