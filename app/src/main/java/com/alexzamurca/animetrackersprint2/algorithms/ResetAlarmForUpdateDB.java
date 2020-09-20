package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.notifications.UpdatingDBChannel;

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

        updatingDBChannel.startAlarmAt4();
    }
}
