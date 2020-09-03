package com.alexzamurca.animetrackersprint2.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;

import com.alexzamurca.animetrackersprint2.series.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class SetNewNotification
{
    private static final String TAG = "SetNewNotification";

    private UpdateSeriesReceiver.OnAirDateListener onAirDateListener;
    private Context context;
    private Series series;
    private Calendar airDateAfterChangesCalendar = null;

    private List<Series> list;

    public SetNewNotification(UpdateSeriesReceiver.OnAirDateListener onAirDateListener, Context context, Series series)
    {
        this.onAirDateListener = onAirDateListener;
        this.context = context;
        this.series = series;
    }

    private void constructCalendar()
    {
        Series newSeries = findSeriesInList(series);
        if(newSeries!=null)
        {
          airDateAfterChangesCalendar = adjustAirDate(newSeries);
        }
    }

    private void setList()
    {
        GetSeriesListAsync getSeriesListAsync = new GetSeriesListAsync();
        getSeriesListAsync.execute();
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

            Calendar calendarNow = Calendar.getInstance(TimeZone.getDefault());
            boolean airDatePassed = calendar.before(calendarNow);

            if(!airDatePassed)
            {
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

                return calendar;
            }
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

            SelectTable selectTable = new SelectTable(session);
            list = selectTable.getSeriesList();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            constructCalendar();
            if(airDateAfterChangesCalendar != null)
            {
                NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context, series, airDateAfterChangesCalendar);
                notificationAiringChannel.setNotification(onAirDateListener);
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
