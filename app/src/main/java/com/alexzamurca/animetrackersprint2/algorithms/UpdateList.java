package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.localList.LocalListStorage;
import com.alexzamurca.animetrackersprint2.localList.UpdateSeriesAiring;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.notifications.SeriesFinishedNotification;
import com.alexzamurca.animetrackersprint2.notifications.UpdateFailedNotification;
import com.alexzamurca.animetrackersprint2.series.AniList.GetSeriesInfo;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.List;

public class UpdateList
{
    private static final String TAG = "UpdateList";
    private Context context;

    UpdateFailedNotification updateFailedNotification;


    public UpdateList(Context context)
    {
        this.context = context;

        updateFailedNotification = new UpdateFailedNotification(context);
    }

    // Get list and update it
    public void run()
    {
        LocalListStorage localListStorage = new LocalListStorage(context);
        ArrayList<Series> list = localListStorage.get();
        updateList(list);
    }

    private void updateList(List<Series> list)
    {
        SharedPreferences appSharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appSharedPreferences.edit();

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
                updateFailedNotification.showNotification();
                editor.putBoolean("need_to_update_list", true);

                Log.d(TAG, "updateList: app set to need_to_update mode");
                editor.apply();
            }

        }
        editor.putBoolean("need_to_update_list", false);
    }

    private void update(Series series, GetSeriesInfo getSeriesInfo)
    {
        String oldStatus = series.getStatus();
        String oldAirDate = series.getAir_date();
        int oldEpisodeNumber = series.getNext_episode_number();
        String newStatus = getSeriesInfo.getStatus();
        Log.d(TAG, "update: series: " + series.getTitle() + " COMPARING oldStatus: " + oldStatus +" AND newStatus: " + newStatus);


        if(oldStatus.equals(newStatus))
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
        UpdateSeriesAiring updateSeriesAiring = new UpdateSeriesAiring(series.getAnilist_id(), episode_number, air_date, status, context);
        updateSeriesAiring.update();

        series.setAir_date(air_date);
        series.setStatus(status);
        series.setNext_episode_number(episode_number);

        NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
        AdjustAirDate adjustAirDate = new AdjustAirDate(series);
        notificationAiringChannel.setNotification(series, adjustAirDate.getCalendar());
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
}
