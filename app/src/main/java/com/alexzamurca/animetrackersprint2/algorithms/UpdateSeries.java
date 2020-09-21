package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.notifications.SeriesFinishedNotification;
import com.alexzamurca.animetrackersprint2.notifications.SetNewNotification;
import com.alexzamurca.animetrackersprint2.notifications.UpdateFailedNotification;
import com.alexzamurca.animetrackersprint2.series.AniList.GetSeriesInfo;
import com.alexzamurca.animetrackersprint2.Database.UpdateSeriesAiring;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

public class UpdateSeries
{
    private static final String TAG = "UpdateSeries";

    private final Series series;
    private Context mContext;
    private String session;
    private boolean set_new_notification;

    public UpdateSeries(Series series, Context mContext)
    {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Account", Context.MODE_PRIVATE);
        session = sharedPreferences.getString("session", "");

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
        CheckConnection checkConnection = new CheckConnection(mContext);
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            Log.d(TAG, "updateAirDate: has internet");
            UpdateAirDateAsync airDateAsync = new UpdateAirDateAsync();
            airDateAsync.setVariables(series, air_date, status, episode_number);
            airDateAsync.execute();
        }
        else
        {
            Log.d(TAG, "updateAirDate: NO INTERNET");

            AppGround appGround = new AppGround();
            boolean isAppOnForeground = appGround.isAppOnForeground(mContext);

            if(isAppOnForeground)
            {
                Log.d(TAG, "updateAirDate request app in foreground");
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update_db", true);
                noConnectionDialog.setArguments(bundle);
                noConnectionDialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "NoConnectionDialog");
            }
            else
            {
                Log.d(TAG, "updateAirDate request app in background");
                UpdateFailedNotification updateFailedNotification = new UpdateFailedNotification(mContext);
                updateFailedNotification.showNotification();

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("App", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("offline", true);
                Log.d(TAG, "insert: app set to offline mode");
                editor.apply();
            }
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
                bundle.putBoolean("update_db", true);
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

                editor.putBoolean("offline", true);
                Log.d(TAG, "insert: app set to offline mode");
                editor.apply();
            }
        }

    }

    private void setNewNotification()
    {
        SetNewNotification setNewNotification = new SetNewNotification(mContext, series);
        setNewNotification.setNotification();
    }

    private class UpdateAirDateAsync extends AsyncTask<Void, Void, Void>
    {
        private Series series;
        private String air_date, status;
        private int episode_number;
        private boolean isSuccessful;


        public void setVariables(Series series, String air_date, String status, int episode_number)
        {
            this.series = series;
            this.air_date = air_date;
            this.status = status;
            this.episode_number = episode_number;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            UpdateSeriesAiring updateSeriesAiring = new UpdateSeriesAiring(session, series.getAnilist_id(), episode_number, air_date, status, mContext);
            isSuccessful = updateSeriesAiring.update() == 0;

            Log.d(TAG, "doInBackground: series:" + series.getTitle() + "was update request successful?:" + isSuccessful);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            String title = series.getTitle();
            if(isSuccessful)
            {
                Toast.makeText(mContext, "\"" + title +"\" has been updated!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onPostExecute: " + "\"" + title +"\" has been updated!");
            }
            else
            {
                Toast.makeText(mContext, "\"" + title +"\" has failed to update!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onPostExecute: " + "\"" + title +"\" has failed to update!");
            }
            
            if(set_new_notification)
            {
                Log.d(TAG, "onPostExecute: updating finished and setting new notification");
                setNewNotification();
            }
            super.onPostExecute(aVoid);
        }
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
