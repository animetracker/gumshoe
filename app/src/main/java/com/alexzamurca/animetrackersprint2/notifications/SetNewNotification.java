package com.alexzamurca.animetrackersprint2.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;

import com.alexzamurca.animetrackersprint2.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.algorithms.AppGround;
import com.alexzamurca.animetrackersprint2.dialog.CheckConnection;
import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import java.util.Calendar;
import java.util.List;

public class SetNewNotification
{
    private static final String TAG = "SetNewNotification";

    private Context context;
    private Series series;
    private Series newSeries;
    private Calendar airDateAfterChangesCalendar = null;

    private List<Series> list;

    public SetNewNotification(Context context, Series series)
    {
        this.context = context;
        this.series = series;
    }

    private void constructCalendar()
    {
        Series tempSeries = findSeriesInList(series);
        if(tempSeries!=null)
        {
            Log.d(TAG, "constructCalendar: found series " + series.getTitle() + " in series list");
            newSeries = tempSeries;
            Calendar tempCalendar =  adjustAirDate(newSeries);
            if(tempCalendar!= null)
            {
                Log.d(TAG, "constructCalendar: airDateAfterChangesCalendar is not null");
                airDateAfterChangesCalendar = tempCalendar;
            }
            else
            {
                Log.d(TAG, "constructCalendar: airDateAfterChangesCalendar is null");
            }
        }
        else
        {
            Log.d(TAG, "constructCalendar: haven't found series " + series.getTitle() + " in series list");
        }
    }

    private void setList()
    {
        CheckConnection checkConnection = new CheckConnection(context);
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            GetSeriesListAsync getSeriesListAsync = new GetSeriesListAsync();
            getSeriesListAsync.execute();
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

    private Series findSeriesInList(Series series)
    {
        for(int i = 0; i < list.size(); i++)
        {
            if( series.getAnilist_id() == list.get(i).getAnilist_id() )
            {
                return list.get(i);
            }
        }
        return null;
    }

    public void setNotification()
    {
        setList();
    }

    private Calendar adjustAirDate(Series series)
    {
        String air_date = series.getAir_date();
        boolean notificationsOn = series.getNotifications_on()==1;
        // If has an air date and notifications are not off
        if(!air_date.equals("") && notificationsOn)
        {
            ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
            Calendar calendar = convertDateToCalendar.convert(air_date);
            Log.d(TAG, "adjustAirDate: calendar before changes for " + series.getTitle() + " is " + convertDateToCalendar.reverseConvert(calendar));

            String air_date_change = series.getAir_date_change();
            String notification_change = series.getNotification_change();

            // If series has a set air date change
            if(!air_date_change.equals(""))
            {
                // get sign, hours, minutes from air_date change
                String[] signHoursMinutesArray  = air_date_change.split(":");
                Character sign = air_date_change.toCharArray()[0];
                int hours = Integer.parseInt(signHoursMinutesArray[0].substring(1));
                int minutes = Integer.parseInt(signHoursMinutesArray[1]);

                if(sign.equals('+'))
                {
                    calendar.add(Calendar.HOUR_OF_DAY, +hours);
                    calendar.add(Calendar.MINUTE, +minutes);
                }
                else if(sign.equals('-'))
                {
                    calendar.add(Calendar.HOUR_OF_DAY, -hours);
                    calendar.add(Calendar.MINUTE, -minutes);
                }
            }
            // If series has a set notification change
            if(!notification_change.equals(""))
            {
                String[] quantityMetricBAArray  = notification_change.split(" ");
                int quantity = Integer.parseInt(quantityMetricBAArray[0]);
                String metric = quantityMetricBAArray[1];
                String beforeAfter = quantityMetricBAArray[2];


                // Add minutes, hours, days
                if(beforeAfter.equals("before"))
                {
                    switch (metric)
                    {
                        case "minutes":
                            calendar.add(Calendar.MINUTE, -quantity);
                            break;
                        case "hours":
                            calendar.add(Calendar.HOUR_OF_DAY, -quantity);
                            break;
                        case "days":
                            calendar.add(Calendar.DAY_OF_MONTH, -quantity);
                            break;
                    }
                }
                else if(beforeAfter.equals("after"))
                {
                    switch (metric)
                    {
                        case "minutes":
                            calendar.add(Calendar.MINUTE, +quantity);
                            break;
                        case "hours":
                            calendar.add(Calendar.HOUR_OF_DAY, +quantity);
                            break;
                        case "days":
                            calendar.add(Calendar.DAY_OF_MONTH, +quantity);
                            break;
                    }
                }
            }
            Log.d(TAG, "adjustAirDate: calendar after changes for " + series.getTitle() + " is " + convertDateToCalendar.reverseConvert(calendar));
            return calendar;

        }
        return null;
    }

    public class GetSeriesListAsync extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
            String session = sharedPreferences.getString("session", "");

            SelectTable selectTable = new SelectTable(session, context);
            list = selectTable.getSeriesList();
            
            if(list != null)
            {
                Log.d(TAG, "doInBackground: got table from db and not null");
            }
            else
            {
                Log.d(TAG, "doInBackground: got table from db and is null");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            constructCalendar();
            if(airDateAfterChangesCalendar != null)
            {
                if(newSeries!=null)
                {
                    Log.d(TAG, "onPostExecute: calendar, listener and newSeries are not null, so setting new notification for \"" + newSeries.getTitle() + "\"");
                    NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
                    notificationAiringChannel.setNotification(newSeries, airDateAfterChangesCalendar);
                }

                Log.d(TAG, "onPostExecute: New notification set");
            }
            else
            {
                Log.d(TAG, "onPostExecute: airDateChangesCalendar is null");
            }

            super.onPostExecute(aVoid);
        }
    }
}
