package com.alexzamurca.animetrackersprint2.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.algorithms.UpdateSeries;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

// This class monitors what happens when the air date of a series is now
// Intent carries the Series info
public class UpdateSeriesReceiver extends BroadcastReceiver
{
    private static final String TAG = "UpdateSeriesReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive: received");
        Bundle args = intent.getBundleExtra("args");
        if(args!=null)
        {
            // getting set_new_notification boolean
            boolean set_new_notification = args.getBoolean("set_new_notification");

            Log.d(TAG, "onReceive: bundle received");
            Series series = (Series) args.getSerializable("series");
            if(series !=null)
            {
                Log.d(TAG, "onReceive: series received"  + series.getTitle());

                UpdateSeries updateSeries = new UpdateSeries(series, context);
                updateSeries.setSeriesInfo(set_new_notification);

            }
        }
    }
}
