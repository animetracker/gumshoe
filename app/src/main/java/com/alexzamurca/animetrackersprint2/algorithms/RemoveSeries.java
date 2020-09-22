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
import com.alexzamurca.animetrackersprint2.Database.Remove;
import com.alexzamurca.animetrackersprint2.notifications.UpdateFailedNotification;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

public class RemoveSeries
{
    private static final String TAG = "RemoveSeries";
    private Context context;
    private String session;

    public RemoveSeries(Context context) {
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
        session = sharedPreferences.getString("session", "");
    }

    public void remove(Series series)
    {
        CheckConnection checkConnection = new CheckConnection(context);
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            Log.d(TAG, "remove: we have internet");
            RemoveAsync removeAsync = new RemoveAsync();
            removeAsync.setSelectedSeries(series);
            removeAsync.execute();
        }
        else
        {
            Log.d(TAG, "remove: NO INTERNET");

            AppGround appGround = new AppGround();
            boolean isAppOnForeground = appGround.isAppOnForeground(context);

            if(isAppOnForeground)
            {
                Log.d(TAG, "remove request app in foreground");
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update_db", true);
                noConnectionDialog.setArguments(bundle);
                noConnectionDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "NoConnectionDialog");
            }
            else
            {
                Log.d(TAG, "remove request app in background");
                UpdateFailedNotification updateFailedNotification = new UpdateFailedNotification(context);
                updateFailedNotification.showNotification();
            }

            SharedPreferences sharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("need_to_update_db", true);
            Log.d(TAG, "insert: app set to need_to_update_db mode");
            editor.apply();
        }


    }

    private class RemoveAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean isSeriesRemoved;
        private Series selectedSeries;

        public void setSelectedSeries(Series selectedSeries)
        {
            this.selectedSeries = selectedSeries;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            Remove remove = new Remove(session, selectedSeries.getAnilist_id(), context);
            isSeriesRemoved = remove.remove();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            String title = selectedSeries.getTitle();
            if(isSeriesRemoved)
            {
                Toast.makeText(context, "\"" + title +"\" is no longer in your series list.", Toast.LENGTH_SHORT).show();
                // Cancel alarm
                NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
                notificationAiringChannel.cancel(selectedSeries);
            }
            else
            {
                Toast.makeText(context, "Failed to remove \"" + title +"\", it is still in your series list.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}
