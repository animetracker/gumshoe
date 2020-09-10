package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.series.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetAlarmsForList
{
    private static final String TAG = "SetAlarmsForList";

    private Context context;

    public SetAlarmsForList(Context context) {
        this.context = context;
    }

    public void run()
    {
        getTableAndSetAlarms();
    }

    private void getTableAndSetAlarms()
    {
        GetTableAsync getTableAsync = new GetTableAsync();
        getTableAsync.execute();
    }

    private void setAllAlarms(List<Series> currentList)
    {
        NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
        for(int i = 0; i < currentList.size(); i++)
        {
            Series selectedSeries = currentList.get(i);

            AdjustAirDate adjustAirDate = new AdjustAirDate(selectedSeries);
            Calendar calendar = adjustAirDate.getCalendar();

            if(calendar!=null)
            {
                Log.d(TAG, "setAllAlarms: set notification for " + selectedSeries.getAir_date());
                notificationAiringChannel.setNotification(selectedSeries, calendar);
            }
        }
    }

    private class GetTableAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean wasRequestSuccessful;
        private ArrayList<Series> list;

        @Override
        protected Void doInBackground(Void... voids)
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
            String session = sharedPreferences.getString("session", "");
            SelectTable selectTable = new SelectTable(session, context);
            list = selectTable.getSeriesList();
            wasRequestSuccessful = selectTable.getWasRequestSuccessful();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(wasRequestSuccessful)
            {
                setAllAlarms(list);
            }

            super.onPostExecute(aVoid);
        }
    }
}
