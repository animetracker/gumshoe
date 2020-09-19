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
        CheckConnection checkConnection = new CheckConnection(context);
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            GetTableAsync getTableAsync = new GetTableAsync();
            getTableAsync.execute();
            Log.d(TAG, "getting table has internet");
        }
        else
        {
            Log.d(TAG, "getting table: NO INTERNET");

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
