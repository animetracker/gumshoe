package com.alexzamurca.animetrackersprint2.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.algorithms.UpdateSeries;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

// This class monitors what happens when the air date of a series is now
// Intent carries the Series info
public class UpdateSeriesReceiver extends BroadcastReceiver
{
    private static final String TAG = "UpdateSeriesReceiver";

    private Context context;
    private Series series;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive: received");

        this.context = context;
        Bundle args = intent.getBundleExtra("args");
        if(args!=null)
        {
            // getting set_new_notification boolean
            boolean set_new_notification = args.getBoolean("set_new_notification");

            Log.d(TAG, "onReceive: bundle received");
            series = (Series) args.getSerializable("series");
            if(series !=null)
            {
                Log.d(TAG, "onReceive: series received"  + series.getTitle());

                UpdateSeries updateSeries = new UpdateSeries(series, context);
                updateSeries.setSeriesInfo();

                // Setting new Notification
                if(set_new_notification)
                {
                    Log.d(TAG, "onReceive: setNewNotification is true");
                    WaitAndSetNewNotification waitAndSetNewNotification = new WaitAndSetNewNotification();
                    waitAndSetNewNotification.execute();
                }

            }
        }
    }

    private void setNewNotification(Context context, Series series)
    {
        SetNewNotification setNewNotification = new SetNewNotification(context, series);
        setNewNotification.setNotification();
    }

    private class WaitAndSetNewNotification extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {
            // Wait one second
            try
            {
                Thread.sleep(60000);
            }
            catch(InterruptedException e)
            {
                Log.d(TAG, "doInBackground: waiting for one minute then setting new notification: InterruptedException");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            setNewNotification(context, series);
            super.onPostExecute(aVoid);
        }
    }
}
