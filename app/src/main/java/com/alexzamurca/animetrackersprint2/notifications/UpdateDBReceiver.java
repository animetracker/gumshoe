package com.alexzamurca.animetrackersprint2.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alexzamurca.animetrackersprint2.algorithms.UpdateDB;

public class UpdateDBReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        UpdateDB updateDB = new UpdateDB(context);
        updateDB.run();
    }
}
