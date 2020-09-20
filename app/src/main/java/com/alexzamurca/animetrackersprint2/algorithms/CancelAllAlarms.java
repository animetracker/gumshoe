package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.alexzamurca.animetrackersprint2.dialog.CheckConnection;
import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.notifications.UpdateFailedNotification;
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
        CheckConnection checkConnection = new CheckConnection(context);
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            GetTableAsync getTableAsync = new GetTableAsync();
            getTableAsync.execute();
            Log.d(TAG, "get table has internet");
        }
        else
        {
            Log.d(TAG, "get table: NO INTERNET");

            AppGround appGround = new AppGround();
            boolean isAppOnForeground = appGround.isAppOnForeground(context);

            if(isAppOnForeground)
            {
                Log.d(TAG, "get table request app in foreground");
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update_db", true);
                noConnectionDialog.setArguments(bundle);
                noConnectionDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "NoConnectionDialog");
            }
            else
            {
                Log.d(TAG, "get table request app in background");
                UpdateFailedNotification updateFailedNotification = new UpdateFailedNotification(context);
                updateFailedNotification.showNotification();

                SharedPreferences sharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("offline", true);
                Log.d(TAG, "insert: app set to offline mode");
                editor.apply();
            }
        }

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
