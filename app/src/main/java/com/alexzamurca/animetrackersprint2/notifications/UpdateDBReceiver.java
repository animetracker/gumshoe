package com.alexzamurca.animetrackersprint2.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.algorithms.UpdateDB;
import com.alexzamurca.animetrackersprint2.dialog.CheckConnection;

public class UpdateDBReceiver extends BroadcastReceiver
{
    private static final String TAG = "UpdateDBReceiver";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive: received");
        UpdatingDBChannel updatingDBChannel = new UpdatingDBChannel(context);
        CheckConnection checkConnection = new CheckConnection(context);
        boolean isConnected  = checkConnection.isConnected();
        if(isConnected)
        {
            Log.d(TAG, "onReceive: have internet connection");
            UpdateDB updateDB = new UpdateDB(context);
            updateDB.run();

            updatingDBChannel.startAlarmAt4();
        }
        else
        {
            Log.d(TAG, "onReceive: no internet connection");
            updatingDBChannel.startAlarm1HourLater();
        }

    }
}
