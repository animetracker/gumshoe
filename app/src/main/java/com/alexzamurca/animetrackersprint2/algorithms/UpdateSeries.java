package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.localList.UpdateSeriesAiring;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.notifications.SeriesFinishedNotification;
import com.alexzamurca.animetrackersprint2.notifications.SetNewNotification;
import com.alexzamurca.animetrackersprint2.notifications.UpdateFailedNotification;
import com.alexzamurca.animetrackersprint2.series.AniList.GetSeriesInfo;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

public class UpdateSeries
{
    private static final String TAG = "UpdateSeries";

    private final Series series;
    private final Context mContext;
    private boolean set_new_notification;

    public UpdateSeries(Series series, Context mContext)
    {
        this.series = series;
        this.mContext = mContext;
    }

    private void update(GetSeriesInfo getSeriesInfo)
    {
        String status = getSeriesInfo.getStatus();
        if(status.equals("FINISHED") || status.equals("CANCELLED"))
        {
            Log.d(TAG, "update: series: " + series.getTitle() + " is now " + status);

            RemoveSeries removeSeries = new RemoveSeries(mContext);
            removeSeries.remove(series);
            Log.d(TAG, "update: series " + series.getTitle() + " was removed from list");

            SeriesFinishedNotification seriesFinishedNotification = new SeriesFinishedNotification(mContext, series, status);
            seriesFinishedNotification.showNotification();
            Log.d(TAG, "update: set notification that " + series.getTitle() + " is now " + status);

            NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(mContext);
            notificationAiringChannel.cancel(series);
        }
        // not null
        else if(!status.equals(""))
        {
            Log.d(TAG, "update: series:" + series.getTitle() + "has a status that is not null (status:" + status + ")");
            String air_date = getSeriesInfo.getAir_date();
            Log.d(TAG, "update: series:" + series.getTitle() + "has air date:" + air_date);
            int episode_number = getSeriesInfo.getEpisode_number();
            Log.d(TAG, "update: series:" + series.getTitle() + "has air date:" + air_date);

            updateAirDate(series, air_date, status, episode_number);
        }
        else
        {
            Log.d(TAG, "update: series:" + series.getTitle() + "is not finished or cancelled (status:" + status + ") and it supposedly has an air date that is null");
        }
    }

    private void updateAirDate(Series series, String air_date, String status, int episode_number)
    {
        UpdateSeriesAiring updateSeriesAiring = new UpdateSeriesAiring(series.getAnilist_id(), episode_number, air_date, status, mContext);
        updateSeriesAiring.update();

        Toast.makeText(mContext, "\"" + series.getTitle() +"\" has been updated!", Toast.LENGTH_LONG).show();
        if(set_new_notification)
        {
            Log.d(TAG, "updateAirDate: updating finished and setting new notification");
            setNewNotification();
        }
    }

    public void setSeriesInfo(boolean set_new_notification)
    {
        CheckConnection checkConnection = new CheckConnection(mContext);
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            Log.d(TAG, "getSeriesInfoMethod: has internet");
            GetSeriesInfoAsync getSeriesInfoAsync = new GetSeriesInfoAsync();
            this.set_new_notification = set_new_notification;
            getSeriesInfoAsync.execute();
        }
        else
        {
            Log.d(TAG, "getSeriesInfo: NO INTERNET");

            AppGround appGround = new AppGround();
            boolean isAppOnForeground = appGround.isAppOnForeground(mContext);

            if(isAppOnForeground)
            {
                Log.d(TAG, "Ani-list getSeriesInfo request app in foreground");
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update_list", true);
                noConnectionDialog.setArguments(bundle);
                noConnectionDialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "NoConnectionDialog");
            }
            else
            {
                Log.d(TAG, "Ani-list getSeriesInfo request app in background");
                UpdateFailedNotification updateFailedNotification = new UpdateFailedNotification(mContext);
                updateFailedNotification.showNotification();

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("App", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("need_to_update_list", true);
                Log.d(TAG, "setSeriesInfo: app set to need_to_update_list mode");
                editor.apply();
            }
        }

    }

    private void setNewNotification()
    {
        SetNewNotification setNewNotification = new SetNewNotification(mContext, series);
        setNewNotification.setNotification();
    }

    private class GetSeriesInfoAsync extends AsyncTask<Void, Void, Void>
    {
        private GetSeriesInfo getSeriesInfo;

        @Override
        protected Void doInBackground(Void... voids)
        {
            getSeriesInfo = new GetSeriesInfo(series.getAnilist_id(), mContext);

            getSeriesInfo.sendRequest();

            Log.d(TAG, "doInBackground: getting series info for series:" + series.getTitle());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            update(getSeriesInfo);
            super.onPostExecute(aVoid);
        }
    }


}
