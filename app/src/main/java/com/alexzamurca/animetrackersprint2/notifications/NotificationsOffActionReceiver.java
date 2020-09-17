package com.alexzamurca.animetrackersprint2.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.alexzamurca.animetrackersprint2.ListFragment;
import com.alexzamurca.animetrackersprint2.series.dialog.NotificationsOffDialog;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

public class NotificationsOffActionReceiver extends BroadcastReceiver
{
    private static final String TAG = "NotificationsOffActionR";
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive: received");
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle!=null)
        {
            Log.d(TAG, "onReceive: bundle not null");
            Series series = (Series) bundle.getSerializable("series");
            if(series!=null)
            {
                Log.d(TAG, "onReceive: series is not null");
                ListFragment listFragment = ListFragment.getInstance();
                NotificationsOffDialog notificationsOffDialog = listFragment.OnNotificationsOffAction(series);
                notificationsOffDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "notificationsOffDialog");
            }
            else
            {
                Log.d(TAG, "onReceive: series is null");
            }
        }
        else
        {
            Log.d(TAG, "onReceive: bundle null");
        }

    }
}
