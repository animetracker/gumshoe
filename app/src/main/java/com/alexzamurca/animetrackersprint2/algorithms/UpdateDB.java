package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.localList.LocalListStorage;
import com.alexzamurca.animetrackersprint2.notifications.DBUpdateFailedNotification;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.notifications.SeriesFinishedNotification;
import com.alexzamurca.animetrackersprint2.notifications.UpdateFailedNotification;
import com.alexzamurca.animetrackersprint2.series.AniList.GetSeriesInfo;
import com.alexzamurca.animetrackersprint2.Database.UpdateSeriesAiring;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.List;

public class UpdateDB
{
    private static final String TAG = "UpdateDB";
    private Context context;
    private String session;
    private boolean failed;

    UpdateFailedNotification updateFailedNotification;


    public UpdateDB(Context context)
    {
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
        session = sharedPreferences.getString("session", "");
        Log.d(TAG, "UpdateDB: session: " + session);

        updateFailedNotification = new UpdateFailedNotification(context);
    }

    // Get table and update db
    public void run()
    {
        LocalListStorage localListStorage = new LocalListStorage(context);
        ArrayList<Series> list = localListStorage.get();
        updateDB(list);

    }

    private void updateDB(List<Series> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            Series currentSeries = list.get(i);
            CheckConnection checkConnection = new CheckConnection(context);
            if(checkConnection.isConnected())
            {
                GetSeriesInfoAsync getSeriesInfoAsync = new GetSeriesInfoAsync();
                getSeriesInfoAsync.setSeries(currentSeries);
                getSeriesInfoAsync.execute();
            }
            else
            {
                failed = true;
                updateFailedNotification.showNotification();

                SharedPreferences appSharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = appSharedPreferences.edit();
                editor.putBoolean("need_to_update_db", true);
                Log.d(TAG, "insert: app set to need_to_update mode");
                editor.apply();
            }

        }
    }

    private void update(Series series, GetSeriesInfo getSeriesInfo)
    {
        String oldStatus = series.getStatus();
        String oldAirDate = series.getAir_date();
        int oldEpisodeNumber = series.getNext_episode_number();
        String newStatus = getSeriesInfo.getStatus();
        Log.d(TAG, "update: series: " + series.getTitle() + " COMPARING oldStatus: " + oldStatus +" AND newStatus: " + newStatus);

        // Error getting new status
        if(newStatus.equals(""))
        {
            DBUpdateFailedNotification dbUpdateFailedNotification = new DBUpdateFailedNotification(context);
            dbUpdateFailedNotification.showNotification();
        }
        else if(oldStatus.equals(newStatus))
        {
            int episode_number = getSeriesInfo.getEpisode_number();
            String air_date = getSeriesInfo.getAir_date();
            if(!oldAirDate.equals(air_date) || episode_number!=oldEpisodeNumber)
            {
                Log.d(TAG, "update: oldAirDate: " + oldAirDate + " COMPARED TO newAirDate: " + air_date);
                Log.d(TAG, "update: oldEpisodeNumber: " + oldEpisodeNumber + " COMPARED TO newEpisodeNumber: " + episode_number);
                Log.d(TAG, "update: status is unchanged but air date or episode number has changed");
                updateAirDate(series, air_date, newStatus, episode_number);
            }
        }
        else
        {
            if( ( oldStatus.equals("RELEASING") && newStatus.equals("FINISHED") ) ||  ((oldStatus.equals("RELEASING") || (oldStatus.equals("NOT_YET_RELEASED"))) && newStatus.equals("CANCELLED")))
            {
                RemoveSeries removeSeries = new RemoveSeries(context);
                removeSeries.remove(series);
                Log.d(TAG, "update: series " + series.getTitle() + " was removed from list");

                SeriesFinishedNotification seriesFinishedNotification = new SeriesFinishedNotification(context, series, newStatus);
                seriesFinishedNotification.showNotification();
                Log.d(TAG, "update: set notification that " + series.getTitle() + " is now " + newStatus);

                NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
                notificationAiringChannel.cancel(series);
            }
            else if(oldStatus.equals("NOT_YET_RELEASED") && newStatus.equals("RELEASING"))
            {
                int episode_number = getSeriesInfo.getEpisode_number();
                String air_date = getSeriesInfo.getAir_date();

                updateAirDate(series, air_date, newStatus, episode_number);
            }
        }
    }

    private void updateAirDate(Series series, String air_date, String status, int episode_number)
    {
        CheckConnection checkConnection = new CheckConnection(context);
        if(checkConnection.isConnected())
        {
            UpdateAirDateAsync airDateAsync = new UpdateAirDateAsync();
            airDateAsync.setVariables(series, air_date, status, episode_number);
            airDateAsync.execute();
        }
        else
        {
            Log.d(TAG, "updateAirDate: updating air date has no connection");
            updateFailedNotification.showNotification();

            SharedPreferences appSharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = appSharedPreferences.edit();
            editor.putBoolean("need_to_update_db", true);
            Log.d(TAG, "insert: app set to need_to_update_db mode");
            editor.apply();
        }
    }

    private class GetSeriesInfoAsync extends AsyncTask<Void, Void, Void>
    {
        private GetSeriesInfo getSeriesInfo;
        private Series series;

        public void setSeries(Series series)
        {
            this.series = series;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            getSeriesInfo = new GetSeriesInfo(series.getAnilist_id(), context);

            getSeriesInfo.sendRequest();

            Log.d(TAG, "doInBackground: getting series info for series:" + series.getTitle());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            update(series, getSeriesInfo);
            super.onPostExecute(aVoid);
        }
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
            Log.d(TAG, "doInBackground: updating series airing in UpdateAirDateAsync with session: " + session);
            UpdateSeriesAiring updateSeriesAiring = new UpdateSeriesAiring(session, series.getAnilist_id(), episode_number, air_date, status, context);
            isSuccessful = updateSeriesAiring.update() == 0;

            Log.d(TAG, "doInBackground: series:" + series.getTitle() + "was update request successful?:" + isSuccessful);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(isSuccessful)
            {
                series.setAir_date(air_date);
                series.setStatus(status);
                series.setNext_episode_number(episode_number);

                NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
                AdjustAirDate adjustAirDate = new AdjustAirDate(series);
                notificationAiringChannel.setNotification(series, adjustAirDate.getCalendar());

                if(!failed)
                {
                    SharedPreferences appSharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = appSharedPreferences.edit();
                    editor.putBoolean("need_to_update_db", false);
                    Log.d(TAG, "insert: app set to need_to_update_db false mode");
                    editor.apply();
                }
            }
            else
            {
                Log.d(TAG, "onPostExecute: " + "\"" + series.getTitle() +"\" has failed to update!");
            }
            super.onPostExecute(aVoid);
        }
    }
}
