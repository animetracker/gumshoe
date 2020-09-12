package com.alexzamurca.animetrackersprint2.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.algorithms.UpdateDB;

public class UpdateDBReceiver extends BroadcastReceiver
{
    private static final String TAG = "UpdateDBReceiver";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive: received");
        UpdateDB updateDB = new UpdateDB(context);
        updateDB.run();
    }
}
