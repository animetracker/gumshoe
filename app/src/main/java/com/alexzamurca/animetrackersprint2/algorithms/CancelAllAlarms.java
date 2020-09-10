package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.series.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.List;

public class CancelAllAlarms
{
    private static final String TAG = "CancelAllAlarms";
    
    private Context context;

    public CancelAllAlarms(Context context) {
        this.context = context;
    }

    public void run()
    {
        getTableAndCancelAlarms();
    }

    private void getTableAndCancelAlarms()
    {
        GetTableAsync getTableAsync = new GetTableAsync();
        getTableAsync.execute();
    }

    private void cancelAllAlarms(List<Series> currentList)
    {
        NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
        for(int i = 0; i < currentList.size(); i++)
        {
            notificationAiringChannel.cancel(currentList.get(i));
            Log.d(TAG, "cancelAllAlarms: cancelled alarm for " + currentList.get(i).getTitle());
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
                cancelAllAlarms(list);
            }

            super.onPostExecute(aVoid);
        }
    }
}
