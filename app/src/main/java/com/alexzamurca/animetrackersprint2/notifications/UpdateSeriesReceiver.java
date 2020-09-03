package com.alexzamurca.animetrackersprint2.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.io.Serializable;

// This class monitors what happens when the air date of a series is now
// Intent carries the Series info
public class UpdateSeriesReceiver extends BroadcastReceiver
{
    private static final String TAG = "UpdateSeriesReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {

        Bundle args = intent.getBundleExtra("args");
        if(args!=null)
        {

            // getting set_new_notification boolean
            boolean set_new_notification = args.getBoolean("set_new_notification");

            Log.d(TAG, "onReceive: bundle received");
            Series series = (Series) args.getSerializable("series");
            if(series !=null)
            {
                Log.d(TAG, "onReceive: series received");

                OnAirDateListener onAirDateListener = (OnAirDateListener) args.getSerializable("onAirDateListener");
                if (onAirDateListener != null)
                {
                    onAirDateListener.onAfterAirDate(series);

                    // Setting new Notification
                    if(set_new_notification)
                    {
                        setNewNotification(onAirDateListener, context, series);
                    }
                }
            }
        }
    }

    private void setNewNotification(OnAirDateListener onAirDateListener, Context context, Series series)
    {
        SetNewNotification setNewNotification = new SetNewNotification(onAirDateListener, context, series);
        setNewNotification.setNotification();
    }

    public interface OnAirDateListener extends Serializable
    {
        void onAfterAirDate(Series series);
    }
}
